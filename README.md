ClimateCraft
============

A minecraft bukkit plugin that adds carbon emission tracking using AMEEconnect (http://www.amee.com).

Licensed under the BSD 3-Clause license (See LICENSE.txt for details)

Author: James Smith (james@floppy.org.uk)

Copyright: Copyright (c) 2011-2013 James Smith & AMEE UK Ltd

Homepage: http://github.com/Floppy/climatecraft


Requirements
------------

* Java Version 6 SDK
* Minecraft 1.5.1
* Craftbukkit 1.5.1-R0.1 (beta)
* An AMEEconnect API key from http://my.amee.com

Installation
------------

1. Download the latest release from (tbc).

2. Place into your craftbukkit plugins folder.

3. When you reload the server, it will probably crash (I'll fix this soon). In the plugins/ClimateCraft/config.yml file, enter your AMEEconnect API key details and all should be well.

Building the code yourself
--------------------------

1. Run ```ant dist```. This builds the whole lot.

2. ```ant deploy``` will copy the plugin to a test/plugins folder. Handy if you have a craftbukkit instance in the test folder...

About the code
--------------

The src/com/amee directory contains all the code which talks to AMEE and manages the
level of carbon in the atmosphere.