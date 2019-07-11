import React from "react";
//import "antd/dist/antd.css";
import {
    PageHeader,
    Typography
} from "antd";
import AddNewAppForm from "../../../components/new-app/AddNewAppForm";

const Paragraph = Typography;

const formConfig = {
    installationType: "WEB_CLIP",
    endpoint: "/web-app",
    jsonPayloadName:"webapp",
    releaseWrapperName: "webAppReleaseWrappers",
    specificElements: {
        url : {
            required: true
        },
        version : {
            required: true
        }
    }
};

class AddNewEnterpriseApp extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            current: 0,
            categories: []
        };
    }

    componentDidMount() {
        // this.getCategories();
    }


    render() {
        return (
            <div>
                <PageHeader
                    title="Add New Web Clip"
                >
                    <div className="wrap">
                        <Paragraph>
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempo.
                        </Paragraph>
                    </div>
                </PageHeader>
                <div style={{background: '#f0f2f5', padding: 24, minHeight: 720}}>
                    <AddNewAppForm formConfig={formConfig}/>
                </div>

            </div>

        );
    }
}

export default AddNewEnterpriseApp;
