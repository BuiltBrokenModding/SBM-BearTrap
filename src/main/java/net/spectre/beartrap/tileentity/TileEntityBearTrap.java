package net.spectre.beartrap.tileentity;

import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.spectre.beartrap.BearConfig;
import net.spectre.beartrap.blocks.BlockBearTrap;
import net.spectre.beartrap.sounds.TSounds;

public class TileEntityBearTrap extends TileEntity implements ITickable{
	
	private UUID trappedEntity;
	private boolean isSet = true;
	private int cooldown = 0;
	private int trappedTicks = 0;
	
	@Override
	public void update() {
		if(!world.isRemote && this.trappedEntity != null) {
			Entity e = ((WorldServer)this.world).getEntityFromUuid(trappedEntity);
			if(e != null && e.getPosition().distanceSq(this.getPos()) > 0.1) {
				if(!BearConfig.teleport || e.getPosition().distanceSq(this.getPos()) < 4) {
					teleportEntity(e, this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5);
					if(this.trappedTicks == 0)
						this.setSet(true);
				}
				else this.setSet(true);
			}
			else if(e == null && !this.isSet) {
				this.setSet(true);
			}
		}
		if(cooldown > 0) {
			--this.cooldown;
		}
		if(this.trappedTicks > 0) {
			--this.trappedTicks;
		}
	}

	public boolean isSet() {
		return this.isSet;
	}
	
	public void setSet(boolean set) {
		this.isSet = set;
		if(set)
			this.trappedEntity = null;
		else this.trappedTicks = BearConfig.timeToEscape;
		world.setBlockState(this.getPos(), world.getBlockState(getPos()).withProperty(BlockBearTrap.OPEN, set));
		cooldown = 20;
		if(!world.isRemote)
			world.playSound(null, this.getPos(), set ? TSounds.RESET : TSounds.TRAP, SoundCategory.BLOCKS, 1F, 1F);
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
	
	public static void teleportEntity(Entity entity, double x, double y, double z) {
		if(!entity.world.isRemote && entity instanceof EntityPlayer) {
			((EntityPlayerMP)entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
		}
		else entity.setPositionAndUpdate(x, y, z);
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return !(newState.getBlock() instanceof BlockBearTrap);
	}
}
