package net.wevboy.alkasi.state.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModProperties
{
	public static final BooleanProperty HAS_FLUID = BooleanProperty.of("has_fluid");
	public static final BooleanProperty BOILING = BooleanProperty.of("boiling");
	public static final BooleanProperty HAS_LID = BooleanProperty.of("has_lid");
	public static final IntProperty TEMPERATURE = IntProperty.of("temperature",0, 15);
}
