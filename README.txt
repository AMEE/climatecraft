== ClimateCraft

A minecraft mod that adds carbon emission tracking using AMEEconnect (http://www.amee.com)

Licensed under the BSD 3-Clause license (See LICENSE.txt for details)

Author: James Smith (james@amee.com)

Copyright: Copyright (c) 2011 AMEE UK Ltd

Homepage: http://github.com/AMEE/climatecraft


== Requirements

* Java Version 6 SDK
* Minecraft 1.0.0


== Building from source code

1) Download MCP 5.0 from http://mcp.ocean-labs.de/index.php/MCP_Releases and put
   it in the same directory as this file.

2) Run 'ant setup'. This will unpack and intialise MCP ready to apply the mod,
   and download required dependencies for the new code.

3) Run 'ant patch'. This will add the climatecraft source code into the code 
   extracted by MCP.

4) Run 'ant build' to compile the code.

5) Run 'ant runclient' to run the minecraft client.
   

== About the code

The src/com/amee directory contains all the code which talks to AMEE and manages the
level of carbon in the atmosphere.

The src/net/minecraft directory contains a set of *patches* to the Minecraft source code
that are applied by the above process.


== Modifying the source code

Changes to the Minecraft source are stored in patches to avoid copyright issues.

To generate these, it's best to create a git repository for the mcp/src directory
after running 'ant setup'. Then, after your changes are made, you can run 
'git diff-files -p' to generate a big patch file, which you can then split up and
add to the climatecraft/src tree along with what's already there.

Note that as the files under com/amee/minecraft do not contain Mojang source code,
they are symlinked into the MCP source tree, so you can edit and commit those 
directly without having to generate patches.

All patch files found under climatecraft/src will be applied during 'ant patch'.