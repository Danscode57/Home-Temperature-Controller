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

// ============= HERE BE A LOT OF ASYNC FUNCTIONS ================ //

function getScheduleUrl() {
    return 'http://' + window.location.hostname + ':8080/schedule'; //'http://192.168.1.78:8080/schedule';//
}

function requestSchedule() {
    return {type: types.SCHEDULE_REQUEST};
}

function scheduleReceived(schedule) {
    return {type: types.SCHEDULE_RECEIVE, schedule};
}

function requestUpdate() {
    return {type: types.SCHEDULE_UPDATE_REQUEST};
}

function receiveSuccessUpdate() {
    return {type: types.SCHEDULE_UPDATE_SUCCESS};
}

function updateError(error) {
    return {type: types.SCHEDULE_UPDATE_FAILURE, error};
}

export function fetchSchedule() {

    return function (dispatch) {
        dispatch(requestSchedule());
        fetch(getScheduleUrl())
            .then(response => response.json())
            .then(json => dispatch(scheduleReceived(json)));
    }

}

export function saveSchedule(schedule) {
    return function (dispatch) {
        dispatch(requestUpdate());

        let requestData = {
            method: 'post',
            body: JSON.stringify(schedule),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        };

        fetch(getScheduleUrl(), requestData)
            .then(response => {
                if (response.status != 200) {
                    dispatch(updateError(response.statusText));
                }
                dispatch(receiveSuccessUpdate())
            });
    }

}