package com.Denfop.ssp.tiles.airpanel;

import com.Denfop.ssp.tiles.TileEntityAirPanel;
import com.Denfop.ssp.tiles.TileEntityMoonPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanel;
import com.Denfop.ssp.tiles.TileEntitySolarPanelsun;

public class TileEntityUltimateHybridSolarair extends TileEntityAirPanel
{
    public static SolarConfig settings;
    
    public TileEntityUltimateHybridSolarair() {
        super(TileEntityUltimateHybridSolarair.settings);
    }
}