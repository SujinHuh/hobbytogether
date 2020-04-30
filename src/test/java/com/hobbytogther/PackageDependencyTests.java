package com.hobbytogther;


import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String HOBBY = "..modules.hobby..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String MAIN = "..modules.main..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.hobbytogether.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.hobbytogether.modules..");

    @ArchTest /** Hobby packge 안에 들어있는 class들은 Event에서만 접근이 가능한 것  */
    ArchRule hobbyPackageRule = classes().that().resideInAPackage(HOBBY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(HOBBY, EVENT, MAIN);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(HOBBY, ACCOUNT, EVENT);

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

    @ArchTest
    ArchRule cycleCheck = slices().matching("com.hobbytogether.modules.(*)..")
            .should().beFreeOfCycles();
}