package net.kunmc.lab.randommorph.mixin;

import draylar.identity.registry.Components;
import net.kunmc.lab.randommorph.RandomMorph;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At(value = "TAIL"))
    private void inject(CallbackInfo ci) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;

        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity == null) {
            return;
        }

        if (this.world.isClient) {
            return;
        }
        Box box = getBoundingBox();

        if (identity instanceof EnderDragonEntity) {
            damageLivingEntities(this.world.getOtherEntities(this, box.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), RandomMorph.CONFIG.enderDragonAttackDamage);
            launchLivingEntities(box, this.world.getOtherEntities(this, box.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), RandomMorph.CONFIG.enderDragonAttackKnockBack);
            if (!RandomMorph.CONFIG.enderDragonBlockBreak) {
                return;
            }
            destroyBlocks(box.expand(1.0), true);
        } else if (identity instanceof RavagerEntity) {
            damageLivingEntities(this.world.getOtherEntities(this, box.offset(0.0D, -2.0D, 0.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), RandomMorph.CONFIG.ravagerAttackDamage);
            launchLivingEntities(box, this.world.getOtherEntities(this, box.offset(0.0D, -2.0D, 0.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR), RandomMorph.CONFIG.ravagerAttackKnockBack);
            if (!RandomMorph.CONFIG.ravagerBlockBreak) {
                return;
            }
            destroyBlocks(box.expand(1.0, 0, 1.0), false);
        }
    }

    private boolean destroyBlocks(Box box, boolean particle) {
        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.floor(box.maxX);
        int maxY = MathHelper.floor(box.maxY);
        int maxZ = MathHelper.floor(box.maxZ);
        boolean bl = false;
        boolean bl2 = false;

        for (int o = minX; o <= maxX; ++o) {
            for (int p = minY; p <= maxY; ++p) {
                for (int q = minZ; q <= maxZ; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (!blockState.isAir() && blockState.getMaterial() != Material.FIRE) {
                        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && !BlockTags.DRAGON_IMMUNE.contains(block)) {
                            bl2 = this.world.removeBlock(blockPos, false) || bl2;
                        } else {
                            bl = true;
                        }
                    }
                }
            }
        }

        if (bl2 && particle) {
            BlockPos blockPos2 = new BlockPos(minX + this.random.nextInt(maxX - minX + 1), minY + this.random.nextInt(maxY - minY + 1), minZ + this.random.nextInt(maxZ - minZ + 1));
            this.world.syncWorldEvent(2008, blockPos2, 0);
        }

        return bl;
    }

    private void damageLivingEntities(List<Entity> entities, double damage) {
        Iterator var2 = entities.iterator();

        while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity instanceof LivingEntity) {
                entity.damage(DamageSource.mob(this), (float) damage);
                this.dealDamage(this, entity);
            }
        }

    }

    private void launchLivingEntities(Box box, List<Entity> entities, double power) {
        double d = (box.minX + box.maxX) / 2.0D;
        double e = (box.minZ + box.maxZ) / 2.0D;
        Iterator var6 = entities.iterator();

        while(var6.hasNext()) {
            Entity entity = (Entity)var6.next();
            if (entity instanceof LivingEntity) {
                double f = entity.getX() - d;
                double g = entity.getZ() - e;
                double h = Math.max(f * f + g * g, 0.1D);
                Vec3d vec3d = new Vec3d(f / h * 4.0D, 0, g / h * 4.0D);
                vec3d = vec3d.multiply(power);
                vec3d = vec3d.add(0, 0.20000000298023224D, 0);
                entity.addVelocity(vec3d.x, vec3d.y, vec3d.z);
            }
        }

    }
}
