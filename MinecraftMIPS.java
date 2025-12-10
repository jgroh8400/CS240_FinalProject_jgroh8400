    package mars.mips.instructions.customlangs;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.mips.instructions.syscalls.*;
    import mars.simulator.*;
    import mars.util.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class MinecraftMIPS extends CustomAssembly{

    private static int MAX_HEALTH = 20;
    private static int MAX_XP = 30;
    private static int MAX_INVENTORY = 64;

    @Override
    public String getName(){
        return "Minecraft MIPS";
    }

    @Override
    public String getDescription(){
        return "Custom langugage that brings Minecraft into MIPS";
    }

    @Override
    protected void populate(){
// basic MIPS instructions
        instructionList.add(
            new BasicInstruction("teleport Label",
            "Teleport : Teleport to specified label",
            BasicInstructionFormat.J_FORMAT,
            "000010 ffffffffffffffffffffffffff",
            new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     Globals.instructionSet.processJump(
                        ((RegisterFile.getProgramCounter() & 0xF0000000)
                                | (operands[0] << 2)));            
                  }
               }));
        instructionList.add(
            new BasicInstruction("toe $t0, $t4, Label",
            "Teleport on Equal : Teleports if health is equal to specified register",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "000100 fffff sssss tttttttttttttttt",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        == RegisterFile.getValue(operands[1]))
                     {
                        Globals.instructionSet.processBranch(operands[2]);
                     }
                  }
            }));
        instructionList.add(            
            new BasicInstruction("tgt $t0, $t4, Label",
            "Teleport if Greater Than : Teleports if health is greater than specified register",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "000101 fffff sssss tttttttttttttttt",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        > RegisterFile.getValue(operands[1]))
                     {
                        Globals.instructionSet.processBranch(operands[2]);
                     }
                  }

            }));
        instructionList.add(
            new BasicInstruction("mine $t4, $t5, $t6",
            "Mine : add blocks to a register, limit of 64",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = RegisterFile.getValue(operands[2]);
                     int sum = 0;
                     if (add1 + add2 < MAX_INVENTORY) {
                        sum = add1 + add2;
                     } else {
                        sum = MAX_INVENTORY;
                     }
                
                     RegisterFile.updateRegister(operands[0], sum);
                  }
                    
            }));
        instructionList.add(            
            new BasicInstruction("dig $t4, $t5, 1",
            "Dig : add blocks to a register with a 16-bit immediate, limit of 64",
            BasicInstructionFormat.I_FORMAT,
            "001000 sssss fffff tttttttttttttttt",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = operands[2] << 16 >> 16;
                     int sum = 0;
                     if (add1 + add2 < MAX_INVENTORY) {
                        sum = add1 + add2;
                     } else {
                        sum = MAX_INVENTORY;
                     }

                     RegisterFile.updateRegister(operands[0], sum);
                  }


                    
            }));
        instructionList.add(            
            new BasicInstruction("build $t4, $t5",
            "Build : Multiply two registers",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 011000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     long product = (long) RegisterFile.getValue(operands[0])
                        * (long) RegisterFile.getValue(operands[1]);
                        if (product > MAX_INVENTORY) {
                            product = MAX_INVENTORY;
                        }

                     RegisterFile.updateRegister(33, (int) (product >> 32));
                     RegisterFile.updateRegister(34, (int) ((product << 32) >> 32));
                  }


                    
            }));
        instructionList.add(            
            new BasicInstruction("destroy $t4, $t5",
            "Destroy : Divide two registers",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 011010",
            new SimulationCode()
                {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     if (RegisterFile.getValue(operands[1]) == 0)
                     {
                        return;
                     }
                  
                     RegisterFile.updateRegister(33,
                        RegisterFile.getValue(operands[0])
                        % RegisterFile.getValue(operands[1]));
                     RegisterFile.updateRegister(34,
                        RegisterFile.getValue(operands[0])
                        / RegisterFile.getValue(operands[1]));
                  }   
            }));
        instructionList.add(            
            new BasicInstruction("place $t4, $t5, $t6",
            "Place : Subtract two registers",
            BasicInstructionFormat.I_FORMAT,
            "000000 sssss ttttt fffff 00000 100010",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int sub1 = RegisterFile.getValue(operands[1]);
                     int sub2 = RegisterFile.getValue(operands[2]);
                     int dif = sub1 - sub2;

                     if (dif < -64) {
                        dif = -64;
                     }
    
                     RegisterFile.updateRegister(operands[0], dif);
                  }        
            }));
        instructionList.add(            
            new BasicInstruction("lb $t4, -100($t5)",
            "Load Block : Set $t4 to block value in specified memory address",
            BasicInstructionFormat.I_FORMAT,
            "100011 ttttt fffff ssssssssssssssss",
            new SimulationCode()
                {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getWord(
                            RegisterFile.getValue(operands[2]) + operands[1]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }           
            }));
        instructionList.add(            
            new BasicInstruction("sb $t4, -100($t5)",
            "Store Block : Store block value in $t4 to specified memory address",
            BasicInstructionFormat.I_FORMAT,
            "101011 ttttt fffff ssssssssssssssss",
            new SimulationCode()
                {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setWord(
                            RegisterFile.getValue(operands[2]) + operands[1],
                            RegisterFile.getValue(operands[0]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  } 
            }));




// unique instructions
        instructionList.add(
            new BasicInstruction("hurt $t0,-100",
            "Hurt : Removes health from $t0",
            BasicInstructionFormat.I_FORMAT,
            "010001 fffff 00000 ssssssssssssssss",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[0]);
                     int add2 = operands[1] << 16 >> 16;
                     int sum = 0;
                     if (add1 - add2 > 0) {
                        sum = add1 - add2;
                     } else {
                        sum = 0;
                     }

                     RegisterFile.updateRegister(operands[0], sum);
                  }
   
            }));
        instructionList.add(
            new BasicInstruction("heal $t0,-100",
            "Heal : Heals player health in $t0",
            BasicInstructionFormat.I_FORMAT,
            "010010 fffff 00000 ssssssssssssssss",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[0]);
                     int add2 = operands[1] << 16 >> 16;
                     int sum = add1 + add2;
                     if (sum < MAX_HEALTH) {
                        sum = add1 + add2;
                     } else {
                        sum = MAX_HEALTH;
                     }

                     RegisterFile.updateRegister(operands[0], sum);
                  }
   
            }));
        instructionList.add(            
            new BasicInstruction("attack",
            "Attack : attack mob for XP, adds random amount of 1-5 to XP register, removing 2 from health register",
            BasicInstructionFormat.I_FORMAT,
            "010110 00000000000000000000000000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int health = RegisterFile.getValue(8);
                    int xp = RegisterFile.getValue(9);

                    Random xprng = new Random();
                    int xpGet = xprng.nextInt(5) + 1;

                    xp += xpGet;

                    if (xp > MAX_XP) {
                        RegisterFile.updateRegister(9, MAX_XP);
                    } else {
                        RegisterFile.updateRegister(9, xp);
                    }

                    if (health < 2) {
                        SystemIO.printString("Health too low to attack.\n");
                    } else {
                        health -= 2;
                        RegisterFile.updateRegister(8, health);
                        SystemIO.printString("Attacked mob. New Health: " + health + " Got " + xpGet + "XP.\n");
                    }

                  }
  
            }));

        instructionList.add(            
            new BasicInstruction("gamemode $s0, 2",
            "Gamemode : set gamemode, 1 for survival (20 health, 64 inventory) 2 for creative (999 health, 999 inventory)",
            BasicInstructionFormat.I_FORMAT,
            "010111 fffff 00000 ssssssssssssssss",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int[] operands = statement.getOperands();
                    int value = operands[1] << 16 >> 16;
                    if (value == 1) {
                        MAX_HEALTH = 20;
                        MAX_INVENTORY = 64;
                        SystemIO.printString("Gamemode Survival. Max health 20. Max inventory 64.\n");
                    } else if (value == 2) {
                        MAX_HEALTH = 999;
                        MAX_INVENTORY = 999;
                        SystemIO.printString("Gamemode Creative. Max health 999. Max inventory 999.\n");
                    }
                  }
     
            }));

        instructionList.add(            
            new BasicInstruction("craft $t6,$t7,$t8",
            "Craft : Crafting table allows for adding between 3 registers. $t5 = $t5 + $t6 + $t7",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 011000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int[] operands = statement.getOperands();
                    int add1 = RegisterFile.getValue(operands[0]);
                    int add2 = RegisterFile.getValue(operands[1]);
                    int add3 = RegisterFile.getValue(operands[2]);

                    int sum = add1 + add2 + add3;

                    if (sum > MAX_INVENTORY) {
                        RegisterFile.updateRegister(operands[0], MAX_INVENTORY);
                    } else {
                        RegisterFile.updateRegister(operands[0], sum);
                    }

                  }
  
            }));
        instructionList.add(            
            new BasicInstruction("smelt $t5,$t6,$t7",
            "Smelt : Furnace allows for subtracting between 3 registers. $t5 = $t5 - $t6 - $t7",
            BasicInstructionFormat.I_FORMAT,
            "000000 sssss ttttt fffff 00000 011001",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int[] operands = statement.getOperands();
                    int sub1 = RegisterFile.getValue(operands[0]);
                    int sub2 = RegisterFile.getValue(operands[1]);
                    int sub3 = RegisterFile.getValue(operands[2]);

                    int sum = sub1 - sub2 - sub3;

                    if (sum < -64) {
                        RegisterFile.updateRegister(operands[1], -64);
                    } else {
                        RegisterFile.updateRegister(operands[1], sum);
                    }

                  }
    
            }));

        instructionList.add(            
            new BasicInstruction("enchant",
            "Enchant : Adds XP level to health",
            BasicInstructionFormat.I_FORMAT,
            "010101 00000000000000000000000000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int xp = RegisterFile.getValue(9);
                    int health = RegisterFile.getValue(8);
                    int result = xp + health;

                    if (result > MAX_HEALTH) {
                        RegisterFile.updateRegister(8, MAX_HEALTH);
                        SystemIO.printString("New health: " + MAX_HEALTH + "\n");
                    } else {
                        RegisterFile.updateRegister(8, result);
                        SystemIO.printString("New health: " + result + "\n");
                    }

                    RegisterFile.updateRegister(9, 0);

                  }
  
            }));

        instructionList.add(            
            new BasicInstruction("armor $t2",
            "Make Armor : With at least 5 ore, craft armor for more health (30 max for iron, 40 max for diamond)",
            BasicInstructionFormat.I_FORMAT,
            "010100 fffff 000000000000000000000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int[] operands = statement.getOperands();
                    int value = RegisterFile.getValue(operands[0]);
                    if (value >= 5 && operands[0] == 10) {
                        SystemIO.printString("Built iron armor. New Health: 30.\n");
                        value -= 5;
                        RegisterFile.updateRegister(operands[0], value);
                        MAX_HEALTH = 30;
                    } else if (value >= 5 && operands[1] == 11) {
                        SystemIO.printString("Built diamond armor. New Health: 40.\n");
                        value -= 5;
                        RegisterFile.updateRegister(operands[0], value);
                        MAX_HEALTH = 40;
                    } else {
                        SystemIO.printString("Not enough ore. Could not craft armor.\n");
                    }

                  }
 
            }));

        instructionList.add(            
            new BasicInstruction("tool $t3",
            "Make Tool : builds tool depending on amount of ore in register (1 for shovel, 2 for sword, 3 for pickaxe)",
            BasicInstructionFormat.I_FORMAT,
            "010011 fffff 000000000000000000000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    int[] operands = statement.getOperands();
                    int value = RegisterFile.getValue(operands[0]);
                    if (operands[0] == 10) {
                        if (value == 1) {
                            value -= 1;
                            SystemIO.printString("Crafted an iron shovel. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);
                        }
                        if (value == 2) {
                            value -= 2;
                            SystemIO.printString("Crafted an iron sword. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);

                        }
                        if (value >= 3) {
                            value -= 3;
                            SystemIO.printString("Crafted an iron pickaxe. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);
                        }
                    }
                    if (operands[0] == 11) {
                        if (value == 1) {
                            value -= 1;
                            SystemIO.printString("Crafted a diamond shovel. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);
                        }
                        if (value == 2) {
                            value -= 2;
                            SystemIO.printString("Crafted a diamond sword. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);
                        }
                        if (value >= 3) {
                            value -= 3;
                            SystemIO.printString("Crafted a diamond pickaxe. New ore: " + value + "\n");
                            RegisterFile.updateRegister(operands[0], value);
                        }
                    }

                  }
 
            }));

        instructionList.add(            
            new BasicInstruction("ore",
            "Find Ore : random chance gives you an ore, stored in $t1 and $t2, also gives 1-5 XP",
            BasicInstructionFormat.I_FORMAT,
            "010000 00000 00000 0000000000000000",
            new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                    Random rng = new Random();
                    int ore = rng.nextInt(5);
                    int initial;

                    if (ore == 4) {
                        initial = RegisterFile.getValue(11);
                        initial += 1;
                        RegisterFile.updateRegister(11, initial);
                        SystemIO.printString("Mined diamond ore. New amount: " + initial + "\n");
                    } else {
                        initial = RegisterFile.getValue(10);
                        initial += 1;
                        RegisterFile.updateRegister(10, initial);
                        SystemIO.printString("Mined iron ore. New amount: " + initial + "\n");
                    }

                  }
            }));
    }
}