package com.idforanimal.utils;


import com.idforanimal.model.APIResponse;
import com.idforanimal.model.CommonModel;

import java.util.ArrayList;
import java.util.Collections;

public class Constant {

    public static String detailsBaseURL = "www.gocamle.com/item-details.php?";
    public static String credentials = "admin:123";
    public static final String ACTION_VIEW_DETAILS = "com.idforanimal.ACTION_VIEW_DETAILS";

    public static String loginBaseUrl = "api-login.php/";
    public static String loginUrl = loginBaseUrl + "login";

    public static String homeBaseUrl = "api-home.php/";
    public static String getSqlFileUrl = homeBaseUrl + "getSqlFile";
    public static String homeListUrl = homeBaseUrl + "getHomeScreenList";
    public static String scanAnimalDetailsUrl = homeBaseUrl + "getAllAnimalDetails";
    public static String stateListUrl = homeBaseUrl + "getAllStateList";
    public static String cityListUrl = homeBaseUrl + "getAllCityList";
    public static String zoneListUrl = homeBaseUrl + "getAllZoneList";
    public static String wardListUrl = homeBaseUrl + "getAllWardList";
    public static String CattlePondList = homeBaseUrl + "getAllCattlePondList";
    public static String animalTypeListUrl = homeBaseUrl + "getAllAnimalTypeList";
    public static String subTypeListUrl = homeBaseUrl + "getAllSubTypeList";
    public static String breedTypeListUrl = homeBaseUrl + "getAllBreedTypeList";
    public static String colorListUrl = homeBaseUrl + "getAllColorList";
    public static String tailListUrl = homeBaseUrl + "getAllTailList";
    public static String hornListUrl = homeBaseUrl + "getAllHornList";

    public static String ownerBaseUrl = "api-animal-owner.php/";
    public static String animalOwnerListUrl = ownerBaseUrl + "getAllAnimalOwnerList";
    public static String addEditAnimalOwnerUrl = ownerBaseUrl + "addEditAnimalOwner";
    public static String animalOwnerDetailsUrl = ownerBaseUrl + "getAnimalOwnerDetails";
    public static String removeAnimalOwnerUrl = ownerBaseUrl + "removeAnimalOwner";

    public static String animalBaseUrl = "api-animal.php/";
    public static String animalListUrl = animalBaseUrl + "getAllAnimalList";
    public static String addEditAnimalUrl = animalBaseUrl + "addEditAnimal";
    public static String animalDetailsUrl = animalBaseUrl + "getAnimalAllDetails";
    public static String removeAnimalUrl = animalBaseUrl + "removeAnimal";
    public static String getAnimalBriefDetails = animalBaseUrl + "getAnimalBriefDetails";
    public static String getAnimalHistory = animalBaseUrl + "getHistory";
    public static String getOwnerIDApi = animalBaseUrl + "getOwnerIDApi";

    public static String catchingAnimalBaseUrl = "api-animal-catching.php/";
    public static String addEditCatching = catchingAnimalBaseUrl + "addEditCatching";
    public static String CatchingList = catchingAnimalBaseUrl + "getCatchingList";
    public static String removeCatching = catchingAnimalBaseUrl + "removeCatching";

    public static String releaseAnimalBaseUrl = "api-animal-release.php/";
    public static String addEditAnimalRelease = releaseAnimalBaseUrl + "addEditAnimalRelease";
    public static String getAllReleaseList = releaseAnimalBaseUrl + "getAllReleaseList";
    public static String removeReleaseAnimal = releaseAnimalBaseUrl + "removeRelease";

    public static String treatmentAnimalBaseUrl = "api-animal-treatment.php/";
    public static String addTreatment = treatmentAnimalBaseUrl + "addTreatment";
    public static String editAnimalDeath = treatmentAnimalBaseUrl + "editAnimalDeath";
    public static String getOperationList = treatmentAnimalBaseUrl + "getOperationList";

    public static String NGOAnimalBaseUrl = "api-ngo.php/";
    public static String addEditNgoTransfer = NGOAnimalBaseUrl + "addEditNgoTransfer";
    public static String getNGOList = NGOAnimalBaseUrl + "getNgoList";


    public static String AUDITAnimalBaseUrl = "api-animal-audit.php/";
    public static String addEditAudit = AUDITAnimalBaseUrl + "addEditAudit";
    public static String auditList = AUDITAnimalBaseUrl + "getAuditList";
    public static String removeAudit = AUDITAnimalBaseUrl + "removeAudit";
    public static String getAnimalAuditDetails = AUDITAnimalBaseUrl + "getAnimalAuditDetails";
    public static String auditAnimalListUrl = AUDITAnimalBaseUrl + "getAuditAnimalList";
    public static String auditAnimalRemarkUrl = AUDITAnimalBaseUrl + "addEditAuditRemark";


    public static boolean _hasLoadedOnce = false;
    public static int listTypeHorizontal = 1;
    public static String loginTypeEmployee = "1";
    public static String loginTypeCustomer = "2";
    public static String typeRFIDTag = "1";
    public static String typeVisualTag = "2";
    public static String TREATMENT = "TREATMENT";
    public static String VACCINATION = "VACCINATION";
    public static String RETAGGING = "RETAGGING";
    public static String TRANSFER = "TRANSFER";
    public static String refreshCount = "refreshCount";


    public static APIResponse getAnimalTypes() {
        CommonModel animal = new CommonModel();
        animal.setAnimalTypeId("4");
        animal.setAnimalName("DOG");

        APIResponse response = new APIResponse();
        response.setAnimalTypeList(new ArrayList<>(Collections.singletonList(animal)));
        response.setMessage("Records found");
        response.setStatus(1);
        return response;
    }

    public static APIResponse getSubTypes() {
        ArrayList<CommonModel> cattleTypes = new ArrayList<>();

        CommonModel female = new CommonModel();
        female.setCattleTypeId("6");
        female.setCattleName("FEMALE");
        cattleTypes.add(female);

        CommonModel male = new CommonModel();
        male.setCattleTypeId("7");
        male.setCattleName("MALE");
        cattleTypes.add(male);

        APIResponse response = new APIResponse();
        response.setSubTypeList(cattleTypes);
        response.setMessage("Records found");
        response.setStatus(1);
        return response;
    }

    public static APIResponse getBreedTypes() {
        ArrayList<CommonModel> breedTypes = new ArrayList<>();

        CommonModel adult = new CommonModel();
        adult.setBreedTypeId("11");
        adult.setBreedName("Adult");
        breedTypes.add(adult);

        CommonModel puppy = new CommonModel();
        puppy.setBreedTypeId("12");
        puppy.setBreedName("Puppy");
        breedTypes.add(puppy);

        APIResponse response = new APIResponse();
        response.setBreedTypeList(breedTypes);
        response.setMessage("Records found");
        response.setStatus(1);
        return response;
    }
}