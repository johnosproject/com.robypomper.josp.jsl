import React from "react";
import Container from '@material-ui/core/Container';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import ReactObjectBase from '../base/ReactObjectBase';

export default class ReactObjectPageBase extends ReactObjectBase {

    constructor(props, title) {
        super(props);
        this.title = title;
        this.objUrl = props.objUrl;
    }

    render() {
        if (!this.isObjectInit())
            return this.renderObjectNotInit();
        if (!this.isObjectAllowed())
            return this.renderObjectNotAllowed();

        const object = this.getObject();
        return (
            <Container>
                <Card style={{margin:'20px 0'}}>
                    <CardContent>
                        {this.state.isFetching
                            ? <Typography variant="h5" component="h3">Loading...</Typography>
                            : (
                                <div>
                                    <Typography variant="h5" component="h3">{this.title}</Typography>
                                    { this.renderPage(object) }
                                </div>
                            )
                        }
                    </CardContent>
                </Card>
            </Container>
        );
    }

}