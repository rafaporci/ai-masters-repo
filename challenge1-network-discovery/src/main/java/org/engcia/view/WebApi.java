package org.engcia.view;

import static spark.Spark.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import org.engcia.engine.tradelanesdiscovery.Engine;
import org.engcia.engine.tradelanesdiscovery.dto.Query;
import spark.Filter;

public class WebApi {
    public void Start()
    {
        corsFilter();
        post("/queryTradeLanes", this::handle);
        post("/simulatePremiumServices", this::handleFC);
        post("/blockLogisticPoint", this::handleBlockLogisticPoint);
        post("/unblockLogisticPoint", this::handleUnblockLogisticPoint);
    }

    public String handle(spark.Request request, spark.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Query query = mapper.readValue(request.body(), Query.class);

        response.header("Content-Type", "application/json");

        return mapper.writeValueAsString(new Engine().Run(query));
    }
    public String handleFC(spark.Request request, spark.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        org.engcia.engine.premiumservicessimulator.dto.Query query = mapper.readValue(request.body(), org.engcia.engine.premiumservicessimulator.dto.Query.class);

        response.header("Content-Type", "application/json");

        return mapper.writeValueAsString(new org.engcia.engine.premiumservicessimulator.Engine().Run(query));
    }
    public String handleBlockLogisticPoint(spark.Request request, spark.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.BlockLogisticPointCommand command = mapper.readValue(request.body(), org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.BlockLogisticPointCommand.class);

        response.header("Content-Type", "application/json");

        if (new org.engcia.engine.tradelanesdiscovery.Engine().BlockLogisticPoint(command))
            response.status(200);
        else
            response.status(500);

        return "";
    }
    public String handleUnblockLogisticPoint(spark.Request request, spark.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.UnblockLogisticPointCommand command = mapper.readValue(request.body(), org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages.UnblockLogisticPointCommand.class);

        response.header("Content-Type", "application/json");

        if (new org.engcia.engine.tradelanesdiscovery.Engine().UnBlockLogisticPoint(command))
            response.status(200);
        else
            response.status(500);

        return "";
    }
    private static void corsFilter() {
        Filter corsFilter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Methods", "POST, OPTIONS");
                response.header("Access-Control-Allow-Headers", "Content-Type");
                response.header("Access-Control-Max-Age", "3600");

                if ("OPTIONS".equals(request.requestMethod())) {
                    response.status(200);
                    response.body("OK");
                } else {
                    response.body("Not OK");
                }
            }
        };
        spark.Spark.before(corsFilter);
    }
}
