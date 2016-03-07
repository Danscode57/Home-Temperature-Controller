require('normalize.css');
require('styles/App.css');

import React, { Component, PropTypes } from 'react';
import Paper from 'material-ui/lib/paper';
import Divider from 'material-ui/lib/divider';
import RaisedButton from 'material-ui/lib/raised-button';
import ContentAdd from 'material-ui/lib/svg-icons/content/add';
import ContentContentCopy from 'material-ui/lib/svg-icons/content/content-copy';

import Hour from './HourComponent';

class DayComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        let { day, daySchedule, actions } = this.props;

        return (
            <Paper zDepth={2} className="day" key={day.key}>
                <h4>{day.name}</h4>
                {daySchedule.hours.map((hour, index) =>
                    <Hour key={index + '-'+ hour.time + '-' + hour.temp} hour={hour} actions={actions} index={index}
                          day={day.index}/>
                )}
                <Divider />
                <div className="container">
                    <div className="item">
                        <RaisedButton secondary={true} label="Add" icon={<ContentAdd />}
                                      onClick={() => actions.addHourSchedule(day.index) } />
                    </div>
                    <div className="item">
                        <RaisedButton primary={true} label="Copy" icon={<ContentContentCopy />}
                                      onClick={() => actions.copyPreviousDay(day.index) } disabled={day.index == 0} />
                    </div>
                </div>
            </Paper>
        );
    }
}

DayComponent.propTypes = {
    day: PropTypes.object.isRequired,
    daySchedule: PropTypes.object.isRequired,
    actions: PropTypes.object.isRequired
};

export default DayComponent;
