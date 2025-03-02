# Database machine

# configure the network
sudo apt update -y
sudo apt install -y net-tools
sudo dhclient eth0
sudo ifconfig eth1 192.168.1.100/24 up
sudo systemctl restart NetworkManager
sudo apt install maven -y

# configure ufw (uncomplicated firewall)
sudo apt install ufw -y
sudo ufw enable
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 5000
sudo ufw reload

# install maven dependencies
cd cryptolib
mvn clean install
cd ../Utils
mvn clean install
cd ..

check_error() {
    if [ $? -ne 0 ]; then
        echo "[ERROR] $1"
        exit 1
    fi
}

echo "==> Deleting the existing PostgreSQL database..."

# sudo systemctl stop postgresql
# check_error "Failed to stop PostgreSQL."

sudo apt-get remove --purge -y "postgresql*"
check_error "Failed to remove PostgreSQL."

sudo rm -rf /etc/postgresql /var/lib/postgresql /var/log/postgresql
check_error "Failed to remove residual PostgreSQL files."

echo "==> Updating repositories..."
sudo apt-get update
check_error "Failed to update repositories."

echo "==> Reinstalling PostgreSQL..."
sudo apt-get install postgresql postgresql-contrib -y
check_error "Failed to install PostgreSQL."

sudo systemctl start postgresql
check_error "Failed to start PostgreSQL service."

# configure postgresql
DB_NAME="t27motorist_db"
DB_USER="t27"
DB_PASSWORD="password123"
POSTGRES_VERSION=$(psql --version | awk '{print $3}' | cut -d '.' -f 1)
OS_USER="postgres"
PG_HBA="/etc/postgresql/$POSTGRES_VERSION/main/pg_hba.conf"
PG_CONF="/etc/postgresql/$POSTGRES_VERSION/main/postgresql.conf"
SQL_FILE1="database/T27-MotorIST-database.sql" # database schema
SQL_FILE2="database/dataloader.sql" # initial data loader

echo "==> Configuring the new database..."

sudo -u $OS_USER psql <<EOF
CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
CREATE DATABASE $DB_NAME OWNER $DB_USER;
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
EOF
check_error "Failed to create user or database."

echo "==> Configuring pg_hba.conf file for MD5 authentication..."

sudo cp $PG_HBA ${PG_HBA}.backup
check_error "Failed to backup pg_hba.conf file."

sudo sed -i 's/^\(local\s\+all\s\+all\s\+\)peer/\1scram-sha-256/' $PG_HBA
check_error "Failed to modify pg_hba.conf file."

echo "==> Restarting PostgreSQL service..."
sudo systemctl restart postgresql
check_error "Failed to restart PostgreSQL service."

echo "==> Ensuring that the user '$DB_USER' has a password set..."
sudo -u $OS_USER psql <<EOF
DO
\$do\$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '$DB_USER') THEN
      CREATE ROLE $DB_USER LOGIN PASSWORD '$DB_PASSWORD';
   ELSE
      ALTER USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
   END IF;
END
\$do\$;
EOF
check_error "Failed to create or configure user $DB_USER."

echo "==> Populating the database with SQL files..."
PGPASSWORD=password123 psql -U $DB_USER -d $DB_NAME -f $SQL_FILE1
check_error "Failed to create tables with SQL file $SQL_FILE1."
PGPASSWORD=password123 psql -U $DB_USER -d $DB_NAME -f $SQL_FILE2
check_error "Failed to populate the database with SQL file $SQL_FILE2."

echo "==> Configuration completed successfully!"
echo "User: $DB_USER"
echo "Database: $DB_NAME"

# run the server