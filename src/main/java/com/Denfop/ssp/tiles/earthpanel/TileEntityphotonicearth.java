package com.Denfop.ssp.tiles.earthpanel;

import com.Denfop.ssp.tiles.TileEntityEarthPanel;
import com.Denfop.ssp.tiles.TileEntityMoonPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanelsun;

public class TileEntityphotonicearth extends TileEntityEarthPanel
{
    public static SolarConfig settings;
    
    public TileEntityphotonicearth() {
        super(TileEntityphotonicearth.settings);
    }

	
}