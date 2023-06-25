package qinomed.qinartificery.mixin;

import com.ordana.spelunkery.blocks.rock_salt.SaltBlock;
import com.ordana.spelunkery.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.ordana.spelunkery.blocks.rock_salt.SaltBlock.*;

@Mixin(SaltBlock.class)
public class SaltBlockMixin extends Block {
    @Mutable
    @Shadow @Final private BlockState crossState;
    private static final BooleanProperty FRAGILE = BooleanProperty.create("fragile");

    public SaltBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void defaultBlockState(BlockBehaviour.Properties properties, CallbackInfo ci) {
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE)
                .setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE)
                .setValue(WEST, RedstoneSide.NONE).setValue(FRAGILE, true));
        this.crossState = this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)
                .setValue(EAST, RedstoneSide.SIDE).setValue(SOUTH, RedstoneSide.SIDE)
                .setValue(WEST, RedstoneSide.SIDE).setValue(FRAGILE, true);
    }

    @Inject(method = "createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V", at = @At(value = "RETURN"))
    private void addFragileProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FRAGILE);
    }

    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN"), cancellable = true)
    private void setFragileToFalseHere(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(this.getConnectionState(context.getLevel(), this.crossState, context.getClickedPos()).setValue(FRAGILE, false));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        if (pState.getValue(FRAGILE))
            return new ArrayList<>();
        return super.getDrops(pState, pBuilder);
    }

    private BlockState getConnectionState(BlockGetter world, BlockState state, BlockPos pos) {
        boolean flag = isDot(state);
        state = this.getMissingConnections(world, this.defaultBlockState(), pos);
        if (!flag || !isDot(state)) {
            boolean flag1 = state.getValue(NORTH).isConnected();
            boolean flag2 = state.getValue(SOUTH).isConnected();
            boolean flag3 = state.getValue(EAST).isConnected();
            boolean flag4 = state.getValue(WEST).isConnected();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;
            if (!flag4 && flag5) {
                state = state.setValue(WEST, RedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                state = state.setValue(EAST, RedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                state = state.setValue(NORTH, RedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                state = state.setValue(SOUTH, RedstoneSide.SIDE);
            }
        }
        return state;
    }

    private BlockState getMissingConnections(BlockGetter world, BlockState state, BlockPos pos) {
        boolean flag = !world.getBlockState(pos.above()).isRedstoneConductor(world, pos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
                RedstoneSide redstoneside = this.getConnectingSide(world, pos, direction, flag);
                state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneside);
            }
        }
        return state;
    }

    private static boolean isDot(BlockState state) {
        return !state.getValue(NORTH).isConnected() && !state.getValue(SOUTH).isConnected() && !state.getValue(EAST).isConnected() && !state.getValue(WEST).isConnected();
    }

    private RedstoneSide getConnectingSide(BlockGetter world, BlockPos pos, Direction dir, boolean canClimbUp) {
        BlockPos blockpos = pos.relative(dir);
        BlockState blockstate = world.getBlockState(blockpos);
        if (canClimbUp) {
            boolean flag = this.canSurviveOn(world, blockpos, blockstate);
            if (flag && canConnectTo(world.getBlockState(blockpos.above()))) {
                if (blockstate.isFaceSturdy(world, blockpos, dir.getOpposite())) {
                    return RedstoneSide.UP;
                }
                return RedstoneSide.SIDE;
            }
        }
        return !canConnectTo(blockstate) && (blockstate.isRedstoneConductor(world, blockpos) || !canConnectTo(world.getBlockState(blockpos.below()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    private boolean canSurviveOn(BlockGetter world, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(world, pos, Direction.UP) || state.is(Blocks.HOPPER);
    }

    protected boolean canConnectTo(BlockState state) {
        return state.is(ModTags.SALT_BLOCKS) || state.is(this);
    }
}
