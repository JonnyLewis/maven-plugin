package com.apim;

import com.apim.service.PluginPropertyValues;
import org.yaml.snakeyaml.Yaml;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws Exception {
        convert();
    }

    public static void jsonTest() {
        JsonObjectBuilder epType = Json.createObjectBuilder().add("endpoint_type", "http");
        epType.add("production_endpoints", Json.createObjectBuilder().add("url", "http://localhost").add("config", ""));
        epType.add("sandbox_endpoints", Json.createObjectBuilder().add("url", "http://localhost").add("config", ""));

        List<String> strings = Arrays.asList("http", "https");
        JsonArray jsonArray = Json.createArrayBuilder().add("http").add("https").build();

        String request = Json.createObjectBuilder()
                .add("name", "DemoAPI")
                .add("description", "Some Description")
                .add("context", "/demo")
                .add("version", "1.0.0")
                .add("provider", "Admin")
                .add("apiDefinition", "HELLO")
                .add("status", "CREATED")
                .add("responseCaching", "Disabled")
                .add("isDefaultVersion", false)
                .add("type", "REST")
                .add("transport", jsonArray)
                .add("tiers", "Gold,Silver")
                .add("visibility", "PRIVATE")
                .add("endpointConfig", epType.build().toString())
                .add("gatewayEnvironments", "Production and Sandbox")
                .build().toString();
        System.out.println(request);
    }

    public static void convert() throws Exception {
        String yamlString = "paths:\\n  /order:\\n    post:\\n      x-auth-type: \\\"Application & Application User\\\"\\n      x-throttling-tier: Unlimited\\n      description: Create a new Order\\n      parameters:\\n        - schema:\\n            $ref: \\\"#/definitions/Order\\\"\\n          description: Order object that needs to be added\\n          name: body\\n          required: true\\n          in: body\\n      responses:\\n        \\\"201\\\":\\n          headers:\\n            Location:\\n              description: The URL of the newly created resource.\\n              type: string\\n          schema:\\n            $ref: \\\"#/definitions/Order\\\"\\n          description: Created.\\n  /menu:\\n    get:\\n      x-auth-type: \\\"Application & Application User\\\"\\n      x-throttling-tier: Unlimited\\n      description: Return a list of available menu items\\n      parameters: []\\n      responses:\\n        \\\"200\\\":\\n          headers: {}\\n          schema:\\n            title: Menu\\n            properties:\\n              list:\\n                items:\\n                  $ref: \\\"#/definitions/MenuItem\\\"\\n                type: array\\n            type: object\\n          description: OK.\\nschemes:\\n  - https\\nproduces:\\n  - application/json\\nswagger: \\\"2.0\\\"\\ndefinitions:\\n  MenuItem:\\n    title: Pizza menu Item\\n    properties:\\n      price:\\n        type: string\\n      description:\\n        type: string\\n      name:\\n        type: string\\n      image:\\n        type: string\\n    required:\\n      - name\\n  Order:\\n    title: Pizza Order\\n    properties:\\n      customerName:\\n        type: string\\n      delivered:\\n        type: boolean\\n      address:\\n        type: string\\n      pizzaType:\\n        type: string\\n      creditCardNumber:\\n        type: string\\n      quantity:\\n        type: number\\n      orderId:\\n        type: string\\n    required:\\n      - orderId\\nconsumes:\\n  - application/json\\ninfo:\\n  title: PizzaShackAPI\\n  description: \\\"This document describe a RESTFul API for Pizza Shack online pizza delivery store.\\\\n\\\"\\n  license:\\n    name: Apache 2.0\\n    url: \\\"http://www.apache.org/licenses/LICENSE-2.0.html\\\"\\n  contact:\\n    email: \\\"architecture@pizzashack.com\\\"\\n    name: John Doe\\n    url: \\\"http://www.pizzashack.com\\\"\\n  version: 1.0.0";
        Yaml yaml = new Yaml();
        Map<String, Object> map = (Map<String, Object>) yaml.load(yamlString);

        JsonObject jsonObject = Json.createObjectBuilder(map).build();
        System.out.println(jsonObject.toString());

    }
}
