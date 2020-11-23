import * as React from 'react';
import {Text, View} from './Themed';
import {ScrollView, StyleSheet} from 'react-native';
import Colors from '../constants/Colors';
import { Ionicons } from '@expo/vector-icons';

export default function DataView(props: any) {
    return(
        <View style={styles.mainblock}>
            <View style={styles.datablock}>
                <Text>Data from the server weather</Text>
                <Ionicons name="ios-partly-sunny" size={48} color="green" />
            </View>

            <View style={styles.datablock}>
                <Text>Data from the server GPS</Text>
                <Ionicons name="ios-locate" size={48} color="green" />
            </View>

            <View style={styles.datablock}>
                <Text>Data from the server SMTH else</Text>
                <Ionicons name="ios-paw" size={48} color="green" />
            </View>

            <View style={styles.datablock}>
                <Text>Data from the server </Text>
                <View style = {styles.ico} >
                    <Ionicons name="ios-thermometer" size={48} color="green" style={{margin:28}}/>
                </View>
                
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    mainblock:{
        width:'96%',
        alignSelf: 'center',

    },
    datablock:{
        width:'100%',
        height: 90,
        borderRadius:10,
        backgroundColor:'orange',
        marginVertical: 5,
        flexDirection: 'row',

    },
    datatoshow:{

    },
    ico:{
        width: '25%',
        alignContent:'center',
        backgroundColor:'orange',
    },
});