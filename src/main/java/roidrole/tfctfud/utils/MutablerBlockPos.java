package roidrole.tfctfud.utils;

import net.minecraft.util.math.BlockPos;

public class MutablerBlockPos extends BlockPos.MutableBlockPos {
	public MutablerBlockPos() {
		super();
	}

	public void translate(int x, int y, int z){
		this.x += x;
		this.y += y;
		this.z += z;
	}
}
