package com.jwt.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;

public class TeamsheetDeserialiser extends JsonDeserializer<Teamsheet> {

    @Override
    public Teamsheet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        // Deserialize Fixture
        JsonNode fixtureNode = rootNode.get("fixture");
        Fixture fixture = new ObjectMapper().treeToValue(fixtureNode, Fixture.class);

        // Deserialize Player
        JsonNode playerNode = rootNode.get("player");
        Player player = new ObjectMapper().treeToValue(playerNode, Player.class);

        // Deserialize Position
        JsonNode positionNode = rootNode.get("position");
        Position position = new ObjectMapper().treeToValue(positionNode, Position.class);

        // Deserialize JerseyNumber

        JsonNode jerseyNumberNode = rootNode.get("jerseyNumber");
        int jerseyNumber = (jerseyNumberNode != null) ? jerseyNumberNode.asInt() : 0; // Set a default value for jerseyNumber if the node is null


        // Create Teamsheet object
        Teamsheet teamsheet = new Teamsheet();
        teamsheet.setFixture(fixture);
        teamsheet.setPlayer(player);
        teamsheet.setPosition(position);
        teamsheet.setJerseyNumber(jerseyNumber);

        return teamsheet;
    }

}
