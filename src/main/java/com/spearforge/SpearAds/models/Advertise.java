package com.spearforge.sIslandAd.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Advertise {

    private String playerName;
    private String category;
    private String adContent;
    private int slot;

}
