import * as React from 'react';
import { Button } from "antd";
import 'antd/dist/antd.css';
import './App.css';
export function SearchSelectButton(props: any) {
    const changeBackageColor = () => {
        return props.code === props.select;
    }
    const getStyle = (): any => {
        let style: any = {};
        for (let key in props.pubStyle) {
            if (props.pubStyle.hasOwnProperty(key)) {
                style[key] = props.pubStyle[key];
            }
        }
        if (changeBackageColor() && props.selectStyle) {
            for (let key in props.selectStyle) {
                if (props.selectStyle.hasOwnProperty(key)) {
                    style[key] = props.selectStyle[key];
                }
            }
        } else if (props.unSelectStyle) {
            for (let key in props.unSelectStyle) {
                if (props.unSelectStyle.hasOwnProperty(key)) {
                    style[key] = props.unSelectStyle[key];
                }
            }
        }
        return style;
    }
    return <Button
        style={getStyle()}
        onClick={props.clickFun(props.code, props.searchDesc)}
    >{props.desc}</Button>
}

export function SearchTab(props:any) {
    return <div className='itab'>
        {props.desc}
    </div>;
}