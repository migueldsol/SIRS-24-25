package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import java.util.List;

public class TestDatabaseOperations {

    public static void main(String[] args) {
        // Teste 1: Testando getCarConfigurations
        testGetCarConfigurations("car1");

        // Teste 2: Testando getTests
        testGetTests("car1");

        // Teste 3: Testando getMaintenanceMode
        testGetMaintenanceMode("car1");

        // Teste 4: Testando setCarConfigurations
        testSetCarConfigurations("car1", "owner1", "{\"color\":\"blue\"}");

        // Teste 5: Testando setMaintenanceMode
        testSetMaintenanceMode("car1", true);

        // Teste 6: Testando setTest
        testSetTest("car1", "mechanic1", "{\"test1\":\"passed\"}");

        // Teste 7: Testando setFirmwareUpdate
        testSetFirmwareUpdate("car1", "1.1.0", "manufacturer1", true);
    }

    // Teste 1: Testando a obtenção das configurações do carro
    private static void testGetCarConfigurations(String carID) {
        System.out.println("Test 1: Get Car Configurations");
        String result = DatabaseOperations.getCarConfigurations(carID);
        System.out.println("Result: " + result);
    }

    // Teste 2: Testando a obtenção dos testes realizados no carro
    private static void testGetTests(String carID) {
        System.out.println("Test 2: Get Tests");
        List<String> tests = DatabaseOperations.getTests(carID);
        System.out.println("Tests: " + tests);
    }

    // Teste 3: Testando a obtenção do modo de manutenção do carro
    private static void testGetMaintenanceMode(String carID) {
        System.out.println("Test 3: Get Maintenance Mode");
        Boolean mode = DatabaseOperations.getMaintenanceMode(carID);
        System.out.println("Maintenance Mode: " + mode);
    }

    // Teste 4: Testando a inserção de novas configurações para o carro
    private static void testSetCarConfigurations(String carID, String userID, String config) {
        System.out.println("Test 4: Set Car Configurations");
        DatabaseOperations.setCarConfigurations(carID, userID, config);
    }

    // Teste 5: Testando a alteração do modo de manutenção do carro
    private static void testSetMaintenanceMode(String carID, Boolean maintenanceMode) {
        System.out.println("Test 5: Set Maintenance Mode");
        DatabaseOperations.setMaintenanceMode(carID, maintenanceMode);
    }

    // Teste 6: Testando a inserção de um teste realizado por um mecânico
    private static void testSetTest(String carID, String mechanicID, String testsPerformed) {
        System.out.println("Test 6: Set Test");
        DatabaseOperations.setTest(carID, mechanicID, testsPerformed);
    }

    // Teste 7: Testando a atualização de firmware no carro
    private static void testSetFirmwareUpdate(String carID, String firmwareVersion, String manufacturerID,
            boolean isInstall) {
        System.out.println("Test 7: Set Firmware Update");
        DatabaseOperations.setFirmwareUpdate(carID, firmwareVersion, manufacturerID, isInstall);
    }
}
