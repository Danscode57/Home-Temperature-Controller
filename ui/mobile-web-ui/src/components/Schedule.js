require('normalize.css');
require('styles/App.css');

import R from 'ramda';
import React from 'react';
import Toggle from 'material-ui/lib/toggle';

import FlatButton from 'material-ui/lib/flat-button';
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';
import ContentSave from 'material-ui/lib/svg-icons/content/save';
import DeviceAccessTime from 'material-ui/lib/svg-icons/device/access-time';
import RefreshIndicator from 'material-ui/lib/refresh-indicator';


import {amber500, blue50} from 'material-ui/lib/styles/colors';

import DayComponent from './schedule/DayComponent';
import { bindActionCreators } from 'redux'
import {connect} from 'react-redux';
import * as ScheduleActions from '../actions/scheduleActions';

class ScheduleComponent extends React.Component {

    constructor(props) {
        super(props);
        this.actions = props.actions;
        this.days = [
            {index: 0, key: 'Mon', prim: false, second: true, name: 'Monday'},
            {index: 1, key: 'Tue', prim: false, second: true, name: 'Tuesday'},
            {index: 2, key: 'Wed', prim: false, second: true, name: 'Wednesday'},
            {index: 3, key: 'Thu', prim: false, second: true, name: 'Thursday'},
            {index: 4, key: 'Fri', prim: false, second: true, name: 'Friday'},
            {index: 5, key: 'Sat', prim: true, second: false, name: 'Saturday'},
            {index: 6, key: 'Sun', prim: true, second: false, name: 'Sunday'}
        ];
        this.state = {currentDay: this.days[0]};
    }

    componentWillMount() {
        this.actions.fetchSchedule();
    }

    daySelected(index) {
        this.setState(R.assoc('currentDay', this.days[index])(this.state));
    }

    showSelectedIcon(index) {
        if (index != this.state.currentDay.index) return '';
        return (<DeviceAccessTime />);
    }

    render() {
        let { active, days } = this.props.schedule;
        let { actions, sync } = this.props;

        let scheduleContent = (
            <p style={{color: amber500, textAlign: 'center'}}>Schedule is disabled</p>
        );

        if (sync.fetchingSchedule) {
            return (
                <div className="loading-container">
                    <RefreshIndicator status="loading" left={200} top={100} className="loading-refresh"/>
                </div>
            );
        }

        if (active) {
            scheduleContent = (
                <div className="week">
                    <Paper className="dayButtons" zDepth={2} style={{backgroundColor: blue50}}>
                        {this.days.map((day, index) => <FlatButton label={day.key} key={day.key} primary={day.prim}
                                                                   labelPosition="before"
                                                                   secondary={day.second}
                                                                   icon={this.showSelectedIcon(day.index)}
                                                                   onTouchTap={ () => this.daySelected(index)}/>)}
                    </Paper>
                    <div>
                        <DayComponent day={this.state.currentDay} daySchedule={days[this.state.currentDay.index]}
                                      actions={actions}/>
                    </div>
                </div>);
        }
        return (
            <div className="schedule">
                <div className="toggle">
                    <div className="container">
                        <div className="item">
                            <Toggle label="Activate" toggled={active}
                                    onToggle={()=>actions.toggleActive()}/>
                        </div>
                        <div className="item">
                            <RaisedButton label="Save changes" primary={true} icon={<ContentSave />}
                                          labelPosition="before"/>
                        </div>
                    </div>
                </div>
                {scheduleContent}
            </div>
        );
    }
}

function scheduleState(state) {
    return state;
}

function mapActions(dispatch) {
    return {actions: bindActionCreators(ScheduleActions, dispatch)};
}

export default connect(scheduleState, mapActions)(ScheduleComponent);