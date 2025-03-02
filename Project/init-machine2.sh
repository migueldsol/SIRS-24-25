# Application server machine

# configure the network
sudo apt update -y
sudo apt install -y net-tools
sudo dhclient eth0
sudo ifconfig eth1 192.168.0.50/24 up
sudo ifconfig eth2 192.168.1.20/24 up
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

# run the server