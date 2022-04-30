package net.wevboy.alkasi;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alkasi implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("alkasi");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
