import * as types from './actionTypes';

export function toggleActive() {
    return {type: types.TOGGLE_SCHEDULE}
}

export function updateTime(day, index, newTime) {
    return {type: types.DAY_SCHEDULE_UPDATE_TIME, day, index, newTime};
}

export function updateTemp(day, index, newTemp) {
    return {type: types.DAY_SCHEDULE_UPDATE_TEMP, day, index, newTemp};
}

export function removeHourSchedule(day, index) {
    return {type: types.DAY_SCHEDULE_REMOVE_HOUR, day, index};
}

export function addHourSchedule(day) {
    return {type: types.DAY_SCHEDULE_ADD_HOUR, day};
}

export function copyPreviousDay(day) {
    return {type: types.DAY_SCHEDULE_COPY, day};
}


function getScheduleUrl() {
    return 'http://' + window.location.hostname + ':8080/schedule';
    //return 'http://192.168.1.84:8080/temp';
}


function requestSchedule() {
    return {type: types.SCHEDULE_REQUEST};
}

function scheduleReceived(schedule) {
    return {type: types.SCHEDULE_RECEIVE, schedule};
}

export function fetchSchedule() {

    return function (dispatch) {
        dispatch(requestSchedule());
        fetch(getScheduleUrl())
            .then(response => response.json())
            .then(json => dispatch(scheduleReceived(json)));
    }

}