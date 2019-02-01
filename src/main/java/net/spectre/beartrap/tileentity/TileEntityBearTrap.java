package net.spectre.beartrap.tileentity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.spectre.beartrap.blocks.BlockBearTrap;

public class TileEntityBearTrap extends TileEntity implements ITickable{
	
	private UUID trappedEntity;
	private boolean isSet = true;
	private int cooldown = 0;
	
	@Override
	public void update() {
		if(!world.isRemote && this.trappedEntity != null) {
			Entity e = ((WorldServer)this.world).getEntityFromUuid(trappedEntity);
			if(e != null && e.getPosition().distanceSq(this.getPos()) > 0.1) {
				e.setPositionAndUpdate(this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5);
			}
		}
		if(cooldown > 0) {
			--this.cooldown;
		}
	}

	public boolean isSet() {
		return this.isSet;
	}
	
	public void setSet(boolean set) {
		this.isSet = set;
		if(set)
			this.trappedEntity = null;
		world.setBlockState(this.getPos(), world.getBlockState(getPos()).withProperty(BlockBearTrap.OPEN, set));
		cooldown = 20;
	}
	
	public void setTrappedEntity(Entity e) {
		this.trappedEntity = e.getUniqueID();
		this.setSet(false);
	}
	
	public boolean isEmpty() {
		return this.trappedEntity == null;
	}

	public int getCooldown() {
		return this.cooldown;
	}
}
