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
public enum ScoreMechanism {
    /*If all ther answers are correct and
    only coorect answers were selected give the score
    otherwise give 0. */
    ALL_OR_NOTHING, 
    /*If all ther answers are correct and
    only coorect answers were selected give the score
    otherwise give score*-1. */
    ALL_OR_MINUS, 
    /*Makes sense only for questins 
    where there is more than one right answer.
    The maximum socre of the question has to be
    equal to the number of right answers.
    With this mechanism we give one point for every right answer
    assuming that no false answer was selected.
    If a false answer was selected no score is given. */
    SCORE_PARTIAL_SUCCESS;
}
