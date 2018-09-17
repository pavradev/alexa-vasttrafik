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
import com.amazon.ask.model.*;
import com.amazon.ask.model.dialog.DelegateDirective;
import com.github.pavradev.alexaskills.vasttrafik.client.VasttrafikClient;
import com.github.pavradev.alexaskills.vasttrafik.client.model.Departure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.amazon.ask.request.Predicates.intentName;

public class NextDepartureIntentHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(NextDepartureIntentHandler.class);

    public static final String VEHICLE_TYPE_SLOT = "vehicleType";
    public static final String VEHICLE_NUMBER_SLOT = "vehicleNumber";
    public static final ZoneId SWEDEN_TIME_ZONE = ZoneId.of("GMT+2");

    private VasttrafikClient vasttrafikClient = new VasttrafikClient();

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("NextDepartureIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;

        Intent intent = intentRequest.getIntent();
        log.info("Intent: {}", intent);

        if (DialogState.STARTED == intentRequest.getDialogState()) {
            // Pre-fill slots: update the intent object with slot values for which
            // you have defaults, then return Dialog.Delegate with this updated intent
            // in the updatedIntent property.
            log.info("Started");
            return input.getResponseBuilder()
                    .addDelegateDirective(intent)
                    .build();
        }

        if (DialogState.IN_PROGRESS == intentRequest.getDialogState()){
            // return a Dialog.Delegate directive with no updatedIntent property.
            log.info("In progress");
            return input.getResponseBuilder()
                    .addDelegateDirective(null)
                    .build();
        }

        Map<String, Slot> slots = intent.getSlots();

        // Get the vehicle number slot from the list of slots.
        Slot vehicleTypeSlot = slots.get(VEHICLE_TYPE_SLOT);
        Slot vehicleNumberSlot = slots.get(VEHICLE_NUMBER_SLOT);
        log.info("Received next departure request for vehicle {} number {}", vehicleTypeSlot, vehicleNumberSlot);


        String speechText;
        try {
            String vehicleNumber = vehicleNumberSlot.getValue();

            LocalDateTime now = LocalDateTime.now(SWEDEN_TIME_ZONE);
            List<Departure> departures = vasttrafikClient.getNextDepartures(now);
            List<Departure> departuresForVehicle = departures.stream()
                    .filter(d -> vehicleNumber.equalsIgnoreCase(d.getSname()))
                    .collect(Collectors.toList());
            log.info("Found {} departures for vehicle {}", departuresForVehicle.size(), vehicleNumber);
            if (departuresForVehicle.isEmpty()) {
                throw new RuntimeException("No departures found");
            }

            LocalDateTime nextDeparture = departuresForVehicle.get(0).getRtDateTime();
            log.info("Next departure: {}", nextDeparture);
            speechText = "Next departure is in " + Duration.between(now, nextDeparture).toMinutes() + " minutes";
        } catch (Exception e) {
            speechText = "Sorry, I could't get next departure time.";
        }
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Vasttrafik", speechText)
                .build();
    }

}
