# Make the changes permanent

The changes you made before will be lost once you perform a reboot of your machine. In order to make them permanent you have to edit the corresponding /etc/network/interfaces file.

To edit the file, run:
```sh
sudo nano /etc/network/interfaces
```
on each machine.

Copy the following into that file:

On VM1 (client):
```sh
source /etc/network/interfaces.d/*

# The loopback network interface and sw-1 interface
auto lo eth1                    
iface lo inet loopback

# sw-1 interface
iface eth01 inet static          
        address 192.168.0.10
        netmask 255.255.255.0
        gateway 192.168.0.50
```

On VM2 (application server):
```sh
source /etc/network/interfaces.d/*

# The loopback network interface, sw-1 interface and sw-2 interface
auto lo eth1 eth2               
iface lo inet loopback

# sw-1 interface
iface eth1 inet static         
        address 192.168.0.50
        netmask 255.255.255.0

# sw-2 interface
iface eth2 inet static          
        address 192.168.1.20
        netmask 255.255.255.0
```

On VM3 (database):
```sh
source /etc/network/interfaces.d/*

# The loopback network interface and sw-2 interface
auto lo eth1                    
iface lo inet loopback

# sw-2 interface
iface eth1 inet static          
        address 192.168.1.100
        netmask 255.255.255.0
        gateway 192.168.1.20
```

Then, restart the `xinetd` service:
```sh
sudo /etc/init.d/xinetd restart
```

If you want to enable xinetd to start automatically on boot, run the following command:
```sh
sudo systemctl enable xinetd
```