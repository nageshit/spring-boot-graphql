package com.vms.graphql.controller;

import com.vms.graphql.model.Player;
import com.vms.graphql.model.Team;
import com.vms.graphql.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;


import static org.junit.jupiter.api.Assertions.*;
@Import(PlayerService.class)
@GraphQlTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    GraphQlTester tester;
    @Autowired
    PlayerService playerService;


    @Test
    void testFindallPlayerShouldReturnAllPlayers(){

        String document = """
                query findAllQuery {
                    findAll {
                        id
                        name
                        team
                    }
                }                
                """;

        tester.document(document)
                .execute()
                .path("findAll")
                .entityList(Player.class)
                .hasSizeGreaterThan(3);
    }


    @Test
    void testValidIdShouldReturnPlayer(){

        String document = """
                query FindOne($id:ID) {
                             findOne(id:$id) {
                                 team
                                 name
                                 id
                             }
                         }         
                """;

        tester.document(document)
                .variable("id",1)
                .execute()
                .path("findOne")
                .entity(Player.class)
                .satisfies(player -> {
                    Assertions.assertEquals("MS Dhoni",player.name());
                    Assertions.assertEquals(Team.CSK,player.team());
                });
    }

    @Test
    void testInValidIdShouldReturnNull(){

        String document = """
                query FindOne($id:ID) {
                             findOne(id:$id) {
                                 team
                                 name
                                 id
                             }
                         }         
                """;

        tester.document(document)
                .variable("id",100)
                .execute()
                .path("findOne")
                .valueIsNull();
    }

    @Test
    void testShouldCreateNewPlayer(){
        int curentCount= playerService.findAll().size();
        String document = """
                mutation Create($name:String,$team:Team) {
                                     create(name:$name,team:$team) {
                                         id
                                         name
                                         team
                                     }
                                 }
                                   
                """;

        tester.document(document)
                .variable("name","Pandiyar")
                .variable("team",Team.RCB)
                .execute()
                .path("create")
                .entity(Player.class)
                .satisfies(player -> {
                    Assertions.assertEquals("Pandiyar",player.name());
                    Assertions.assertEquals(Team.RCB,player.team());
                });

        Assertions.assertEquals(curentCount+1,playerService.findAll().size());
    }

    @Test
    void testShouldUpdatePlayer(){
        int curentCount= playerService.findAll().size();
        String document = """
                mutation Update($id:ID,$name:String,$team:Team) {
                                     update(id:$id,name:$name,team:$team) {
                                         id
                                         name
                                         team
                                     }
                                 }
                                   
                """;

        tester.document(document)
                .variable("id",3)
                .variable("name","Pandiyar")
                .variable("team",Team.CSK)
                .execute()
                .path("update")
                .entity(Player.class);
         Player updatePlayer = playerService.findOne(3).get();

        Assertions.assertEquals("Pandiyar",updatePlayer.name());
    }


    @Test
    void testShouldDeletePlayer(){
        int curentCount= playerService.findAll().size();
        String document = """
                mutation Delete($id:ID) {
                                     delete(id:$id) {
                                         id
                                         name
                                         team
                                     }
                                 }
                                   
                """;

        tester.document(document)
                .variable("id",3)
                .executeAndVerify();

        Assertions.assertEquals(curentCount-1,playerService.findAll().size());
    }



}