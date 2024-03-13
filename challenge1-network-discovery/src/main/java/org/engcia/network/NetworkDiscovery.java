package org.engcia.network;

import org.engcia.view.WebApi;

public class NetworkDiscovery {
    public static void main(String[] args) {
        // for tests only
        //new org.engcia.engine.premiumservicessimulator.Engine().Test();
        new WebApi().Start();
    }
}

