# Client machine

# install needed dependencies and configure the network
sudo apt update -y
sudo apt install -y net-tools
sudo dhclient eth0
sudo ifconfig eth1 192.168.0.10/24 up
sudo systemctl restart NetworkManager
sudo apt install maven -y

# install maven dependencies
cd cryptolib
mvn clean install
cd ../Utils
mvn clean install
cd ..

# run the server