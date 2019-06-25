package com.doorfail.scramblecraft.entity.mob;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class Meep extends EntityMob {
    Meep(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected  void initEntityAI()
    {
        this.tasks.addTask(0,new EntityAILookIdle(this));
        this.tasks.addTask(1,new EntityAIWander(this,0.50D));
        this.targetTasks.addTask(0, new EntityAIAttackMelee(this,1.0D,true));
    }
}
