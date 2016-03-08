import * as types from '../actions/actionTypes';
import R from 'ramda';

const initialState = {
    fetchingSchedule: false,
    savingSchedule: false,
    scheduleChanged: false
};


export default function syncReducer(state = initialState, action){
    switch(action.type){
        case types.SCHEDULE_REQUEST:
            return R.assoc('fetchingSchedule', true)(state);
        case types.SCHEDULE_RECEIVE:
            return R.assoc('fetchingSchedule', false)(state);
        case types.SCHEDULE_UPDATE_REQUEST:
            return R.assoc('savingSchedule', true)(state);
        case types.SCHEDULE_UPDATE_SUCCESS:
        case types.SCHEDULE_UPDATE_FAILURE:
            return R.assoc('scheduleChanged', false)(R.assoc('savingSchedule', false)(state));
        case types.DAY_SCHEDULE_ADD_HOUR:
        case types.DAY_SCHEDULE_COPY:
        case types.DAY_SCHEDULE_REMOVE_HOUR:
        case types.DAY_SCHEDULE_UPDATE_TEMP:
        case types.DAY_SCHEDULE_UPDATE_TIME:
        case types.TOGGLE_SCHEDULE:
            return R.assoc('scheduleChanged', true)(state);
        default:
            return state;
    }

}