README


Minecraft MIPS is a custom language for MARS LE that brings some of the functionality of Minecraft into MIPS. The language can be installed by dropping the MinecraftMIPS.jar and .java files into the customlangs folder inside MARS LE, and then switching to the language using the Language Switcher tool inside MARS LE.

There are 20 implemented instructions, with 10 basic MIPS instructions and 10 unique instructions.

Full Instruction Set

Basic MIPS instructions         Opcodes

Teleport                                           000010
Teleport on Equal                            000100
Teleport if Greater Than                  000101
Mine                                                special/ 100000
Dig                                                   001000
Build                                                special / 011000
Destroy                                           special/ 011010
Place                                               special/ 100010
Load Block                                      100010
Store Block                                      100011

Unique MIPS instructions

Find Ore                                          010000
Hurt                                                 010001
Heal                                                010010
Make Tool                                       010011
Make Armor                                    010100
Enchant                                          010101
Attack                                             010110
Gamemode                                    010111
Craft                                               special/011000
Smelt                                              special/011001
