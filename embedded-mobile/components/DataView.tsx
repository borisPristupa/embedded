import * as React from 'react';
import {Text, View} from './Themed';
import {ScrollView, StyleSheet} from 'react-native';
import Colors from '../constants/Colors';
import { Ionicons } from '@expo/vector-icons';

export default function DataView(props: any) {
    return(
        <View style={styles.mainblock}>
            <View style={styles.datablock}>
                <Text style={styles.datatoshow}>Data from the server weather</Text>
                <Ionicons name="ios-partly-sunny" size={48} color="white" />
            </View>

            <View style={styles.datablock}>
                <Text style={styles.datatoshow}>Data from the server GPS</Text>
                <Ionicons name="ios-locate" size={48} color="white" />
            </View>

            <View style={styles.datablock}>
                <Text style={styles.datatoshow}>Data from the server SMTH else</Text>
                <Ionicons name="ios-paw" size={48} color="white" />
            </View>

            <View style={styles.datablock}>
                <Text style={styles.datatoshow}>Data from the server </Text>
                <Ionicons name="ios-thermometer" size={48} color="white" />   
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    mainblock:{
        width:'96%',
        alignSelf: 'center',
        backgroundColor: 'rgba(0, 0, 0, 0)',

    },
    datablock:{
        width:'100%',
        height: 90,
        borderRadius:10,
        borderWidth: 2,
        borderColor: "white",
        backgroundColor:'rgba(0, 0, 0, 0)',
        marginVertical: 5,
        flexDirection: 'row',

    },
    datatoshow:{
        color: "white",
        fontWeight: "bold",
        margin:5,
    },
});