package com.Denfop.ssp.tiles.firepanel;

import com.Denfop.ssp.tiles.TileEntityMoonPanel;
import com.Denfop.ssp.tiles.TileEntityMoonPanel1;
import com.Denfop.ssp.tiles.TileEntityNetherPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanelsun;

public class TileEntityAdminfire extends TileEntityNetherPanel
{
    public static SolarConfig settings;
    
    public TileEntityAdminfire() {
        super(TileEntityAdminfire.settings);
    }

	
}