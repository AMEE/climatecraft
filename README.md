ClimateCraft
============

A minecraft mod that adds carbon emission tracking using AMEEconnect (http://www.amee.com). 
Currently single-player only.

Licensed under the BSD 3-Clause license (See LICENSE.txt for details)

Author: James Smith (james@floppy.org.uk)

Copyright: Copyright (c) 2011 James Smith & AMEE UK Ltd

Homepage: http://github.com/Floppy/climatecraft


Requirements
------------

* Java Version 6 SDK
* Minecraft 1.5.1


Building from source code
-------------------------

1. Download MCP 7.44 from http://mcp.ocean-labs.de/index.php/MCP_Releases and put
   it in the same directory as this file.
   
2. Run ```ant setup```. This will unpack and intialise MCP ready to apply the mod,
   and download required dependencies for the new code.

3. Run ```ant patch```. This will add the climatecraft source code into the code 
   extracted by MCP.

4. Run ```ant build``` to compile the code.

5. Run ```ant runclient``` to run the minecraft client.
   

About the code
--------------

The src/com/amee directory contains all the code which talks to AMEE and manages the
level of carbon in the atmosphere.

The src/mcp.patch file contains a set of *patches* to the Minecraft source code
that are applied by the above process.


Modifying the source code
-------------------------

Changes to the Minecraft source are stored in a patch to avoid copyright issues.

To generate these, ```ant setup``` initialises a git repository in the MCP source directory.
You can make changes, test, then when you're done, run ```ant generate_patch``` to generate 
the updated patch file.

Note that as the files under com/amee/minecraft do not contain Mojang source code,
they are symlinked into the MCP source tree, so you can edit and commit those 
directly without having to generate patches.