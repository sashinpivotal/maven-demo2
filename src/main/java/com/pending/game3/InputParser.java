package com.pending.game3;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class InputParser {

    // getInput()
    boolean getInput(Scanner userInput){
        System.out.print("Enter Command\n> "); // allows input command to be on next line
        String input = userInput.nextLine().toLowerCase(); // Reads user input

        String[] inputSplit = input.split(" ", 2); // splits array in 2 after 1st space
        SynonymDictionary command = null;
        try{
            command = SynonymDictionary.valueOf(inputSplit[0].toUpperCase()); //command = verb
        } catch (Exception e){
            if (SynonymDictionary.GO.synonyms.contains(inputSplit[0])) { //if a synonym of "go" in syn dict use go
                command = SynonymDictionary.GO;
                //if a synonym of "inspect" in syn dict use inspect....
            } else if (SynonymDictionary.INSPECT.synonyms.contains(inputSplit[0])) {
                command = SynonymDictionary.INSPECT;
            } else if (SynonymDictionary.TAKE.synonyms.contains(inputSplit[0])) {
                command = SynonymDictionary.TAKE;
            } else if (SynonymDictionary.QUIT.synonyms.contains(inputSplit[0])) {
                command = SynonymDictionary.QUIT;
            } else if (SynonymDictionary.INFO.synonyms.contains(inputSplit[0])) {
                command = SynonymDictionary.INFO;
            }// TODO: else if (check the rest of SynonymDict for matches)
            else {
                System.out.println("command " + input +
                        " not recognized. enter \"Info\" for a list of valid commands.");
                return false;
            }
        }
        switch (command){
            case REPLAY:
                Game3.runProgram();
                return true;
            case GO:
                HashMap<String,  String> connectionsMap = Game3.getCurrentRoom().getConnections();
                if (connectionsMap.containsKey(inputSplit[1])) {
                    goToRoom(connectionsMap.get(inputSplit[1]));
                } else {
                    System.out.println("There's nowhere to go that direction.");
                }
                break;
            case INSPECT:
                if("room".equalsIgnoreCase(inputSplit[1])){
                System.out.println(Game3.getCurrentRoom().description);
                } else if (Game3.getCurrentRoom().getItems().contains(inputSplit[1])) {
                        System.out.println(Game3.getItems().get(inputSplit[1]).description);
                    } else {
                        System.out.println("Item does not exist");
                    }
                break;
            case TAKE:
                    if (Game3.getCurrentRoom().getItems().contains(inputSplit[1])) {
                    Game3.getCurrentRoom().takeItem(inputSplit[1]);
                    }
                break;
            case QUIT:
                return true;
            case INFO:
                for (SynonymDictionary synDict: SynonymDictionary.values()) {
                    System.out.println("Command: " + synDict.name() + " valid aliases: " + synDict.synonyms);
                }
            default:
                System.out.println("Command not yet supported");
       }
                return false;
    }

    private void goToRoom(String destination) {
        if (Game3.getRooms().containsKey(destination)) {
            Room destinationRoom = Game3.getRooms().get(destination);
            if (destinationRoom.getFlags().containsKey("Locked")) {
                //get the lock and check inventory for matching key
                List<String> keyData = destinationRoom.getFlags().get("Locked");
                if (keyData.size() > 0){
                    if(keyCheck(keyData.get(0))){
                        destinationRoom.editFlags().remove("Locked");
                        Game3.setCurrentRoom(destinationRoom);
                    } else {
                        System.out.println("The door is locked.");
                    }
                }else{
                    System.out.println("Locked flag on Room " + destination +
                            " has no data to identify the correct key with.");
                }
                //then remove "Locked" flag, move to new room, and return true;
            } else { //move to destination room and confirm move completed.
                Game3.setCurrentRoom(destinationRoom);
            }
        } else {
            System.out.println("Room " + destination + " does not exist.");
        }
    }

    private boolean keyCheck(String keyId) {
        for (String itemName : Game3.getInventory()){
            if(Game3.getItems().containsKey(itemName)){
                Item item = Game3.getItems().get(itemName);
                HashMap<String, List<String>> itemFlags = item.getFlags();
                if(itemFlags.containsKey("Key") && itemFlags.get("Key").contains(keyId)){
                    return true;
                }
            }
        }
        return false;
    }

}