import React from "react";
import "antd/dist/antd.css";
import {PageHeader, Typography, Input, Button, Row, Col} from "antd";


const Search = Input.Search;

const routes = [
    {
        path: 'index',
        breadcrumbName: 'Publisher',
    },
    {
        path: 'first',
        breadcrumbName: 'Dashboard',
    },
    {
        path: 'second',
        breadcrumbName: 'Apps',
    },
];


class Release extends React.Component {
    routes;

    constructor(props) {
        super(props);
        this.routes = props.routes;

    }

    componentDidMount() {

    }

    render() {
        const {uuid} = this.props.match.params;
        return (
            <div>
                <PageHeader
                    breadcrumb={{routes}}
                />
                <div style={{background: '#f0f2f5', padding: 24, minHeight: 780}}>
                    <Row style={{padding: 10}}>
                        <Col span={6} offset={18}>
                            <Search
                                placeholder="search"
                                onSearch={value => console.log(value)}
                                style={{width: 200}}
                            />
                            <Button style={{margin: 5}}>Advanced Search</Button>
                        </Col>
                    </Row>
                </div>

            </div>

        );
    }
}

export default Release;
