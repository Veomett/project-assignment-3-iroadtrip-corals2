# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

The main data structures used in my implementation os IRoadTrip are: 
    HashMap<String, String> name2id: connects countries names to their stateid
    HashMap<String, Integer> distances: country stateids combined as keys and km between capitols as value
    HashMap<String, ArrayList<String>> adjacents: stateid of a country as key and list of stateids of countries that border it as values
    HashMap<String, HashMap<String, Integer>> bighash: stateid of country as key and hashmap (smallhash) as value. smallhash has stateids
      of the countries that border the country of the key in the bighash, and the distance between their capitols as values

These data structures are built when the constructor of IRoadTrip is called. It employs helper methods
        buildname2id(state_name): reads state_name.tsv file and makes HashMap "name2id." Special cases of "()" and "/" are accounted for
            and some edgecases in which names are inconsistent between border.txt and state_name.tsv are added to name2id as well. Two               
            countries that are in both the border.txt file and state_name.tsv file but not the capdist.csv file because of the
            recency of the countries' creation are removed from name2id to maintain consistency. 
        builddistances(capdist): read capdist.csv file and make HashMap "distances." Stateids of the countries the distance is between 
            are put together to be the key (String). The value (Integer) is the kilometer distance between their capitols.
        buildadjacents(borders):  create HashMap "adjacents" of stateids using borders.txt file. key is the stateid of the country before the 
            "=" and value is an arraylist of the stateids of the countries after the "="
        buildbigHash(adjacents): builds HashMap "bighash" with stateids as keys gotten from name2id and a HashMap "smallhash" as value. 
            smallhash holds all the bordering country's stateids as keys and km distance between the capitols as values
