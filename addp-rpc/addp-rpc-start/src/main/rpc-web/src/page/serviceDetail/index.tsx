import * as React from 'react';
import '../../App.css';
import { Table } from "antd";
import { SearchSelectButton } from "../../MyComponent"
import 'antd/dist/antd.css';
import { setNavType } from '../../rpc-redux/actions/index'
import { withRouter } from 'react-router-dom';
import { connect, ConnectedComponentClass } from 'react-redux';
interface IServiceTableType {
    ip: string,
    port: number,
}

const searchSelectStyle = {
    border: 'none',
    borderRadius: '10px 10px 0 0',
    minWidth: '100px'
}

class ServiceDetail extends React.Component<any, any> {
    public state = {
        detailSelectType: 'producer',
        serviceDetailTable: []
    }
    constructor(props: any) {
        super(props);
        window['AddpContext']['ServiceDetail'] = {};
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['ServiceDetail'] = this.state);
    }
    // 详情界面纬度选择按钮时间
    public selectDetailType = (detailSelectType: 'producer' | 'consumer') => () => {
        this.setState({ detailSelectType });
    }
    public render() {
        console.log("ServiceDetail start...",this.props)
        this.props.dispatch(setNavType('service_detail'));
        return (
            <div className='conetntDiv'>
                <h4>服务器列表</h4>
                <div>
                    <SearchSelectButton desc='服务发布者' code='producer'
                        select={this.state.detailSelectType}
                        clickFun={this.selectDetailType}
                        pubStyle={searchSelectStyle}
                        selectStyle={{ borderColor: 'rgb(240, 242, 245)', borderStyle: 'ridge', borderBottomStyle: 'none', borderWidth: 'initial' }}
                        unSelectStyle={{ borderColor: '#fff', borderWidth: 'initial' }}
                        searchDesc='服务发布者' />
                    <SearchSelectButton desc='服务消费者' code='consumer'
                        select={this.state.detailSelectType}
                        clickFun={this.selectDetailType}
                        pubStyle={searchSelectStyle}
                        selectStyle={{ borderColor: 'rgb(240, 242, 245)', borderStyle: 'ridge', borderBottomStyle: 'none', borderWidth: 'initial' }}
                        unSelectStyle={{ borderColor: '#fff', borderWidth: 'initial' }}
                        searchDesc='服务消费者' />
                </div>
                <Table<IServiceTableType> dataSource={this.props.redux.data.serviceDetailTable} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                    <Table.Column<IServiceTableType> title="服务方IP" dataIndex="ip" width="70%" />
                    <Table.Column<IServiceTableType> title="服务方端口" dataIndex="port" width="30%" />
                </Table>
            </div>
        );
    }
}
export default withRouter((connect(
    (state: any) => {
        return {
            redux: state.detail
        }
    },
    (dispatch) => ({
        dispatch: (func: any) => func(dispatch)
    }),
    null,
    { withRef: false }
)(ServiceDetail) as ConnectedComponentClass<typeof ServiceDetail, any>));