/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities.enums;

/**
 *
 * @author peti
 */
public enum PossibleAnswerType {
    /*free text field, short*/
    shortText,
    /*free text field, long*/
    longText,
    /*dropdown, only one PossibleAnswerValue can be true*/
    dropdown,
    /*radio buttons, only one PossibleAnswerValue can be true*/
    radioButtons,
    /*checkbox, one PossibleAnswerValue only*/
    trueFalseCheckbox,
    /*many checkbox, any number of PossibleAnswerValue can be true*/
    checkboxList,
    /*PossibleAnswerValue contains lines of source code, order is specified via rightOrder field
    * Order will be checked based on the text of the PossibleAnswerValue.
    *  Use this when the source code is  simple enough, so that only one right ordering is possible*/
    soruceCodeToOrder_EvalByCompare,
    /*PossibleAnswerValue contains lines of source code, order is specified via rightOrder field
     * Order will be checked based on the text of the PossibleAnswerValue.
     *  Use this when the source code is complex, more then one right ordering is possible.
     * The evaluation algorithm will run the source code.
     * Source code and test code should be specified in sourceCode and unitTestCode fields.*/
    soruceCodeToOrder_EvalByRun;
}
