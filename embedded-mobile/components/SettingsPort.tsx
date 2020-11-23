import * as React from 'react';
import {Text, View} from './Themed';
import {ScrollView, StyleSheet} from 'react-native';

import ModalSelector from 'react-native-modal-selector'


export default function SettingsPort(props: any) {
    const [speed, setSpeed] = React.useState(9600)

    let index = 0;
    const data = [
        { key: index++, section: true, label: '9600' },
        { key: index++, label: '19200' },
        { key: index++, label: '38400' },
        { key: index++, label: '57600' },
        { key: index++, label: '115200' },
    ];



    return(
        <View style={styles.ch_box}>
            <Text>Port #{props.number}</Text>
            <Text>Speed</Text>             
            <ModalSelector
                    data={data}
                    initValue="Select port speed!"
                    onChange={(option)=>{ 
                        console.log(option.label);
                        }} 
                    animationType={"slide"}
                    selectTextStyle={styles.text}
                    selectStyle={styles.selectmodelstyle}
                    />
        </View>
    );
}


const styles = StyleSheet.create({
    ch_box:{
        width: '98%',
        backgroundColor:'#fff',
        margin: 3,
    },
   selectmodelstyle:{
        backgroundColor:'black',
        borderRadius:10,
   },
   text:{
        color: 'white',
   },
});

