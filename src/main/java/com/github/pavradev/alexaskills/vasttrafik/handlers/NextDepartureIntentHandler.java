/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package com.github.pavradev.alexaskills.vasttrafik.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.github.pavradev.alexaskills.vasttrafik.client.VasttrafikClient;
import com.github.pavradev.alexaskills.vasttrafik.client.model.Departure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class NextDepartureIntentHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(NextDepartureIntentHandler.class);

    public static final ZoneId SWEDEN_TIME_ZONE = ZoneId.of("GMT+2");

    private VasttrafikClient vasttrafikClient = new VasttrafikClient();

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("NextDepartureIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText;
        try {
            LocalDateTime now = LocalDateTime.now(SWEDEN_TIME_ZONE);
            List<Departure> departures = vasttrafikClient.getNextDepartures(now);
            if(departures == null || departures.isEmpty()) {
                speechText = "Sorry, I could't get next departure time.";
            } else {
                LocalDateTime nextDeparture = departures.get(0).getRtDateTime();
                log.info("Next departure: {}", nextDeparture);
                speechText = "Next departure is in " + Duration.between(now, nextDeparture).toMinutes() + " minutes";
            }
        } catch (Exception e) {
            speechText = "Sorry, I could't get next departure time.";
        }
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Vasttrafik", speechText)
                .build();
    }

}
