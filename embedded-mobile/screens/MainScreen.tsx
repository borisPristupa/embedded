import * as React from 'react';
import {ScrollView, StyleSheet} from 'react-native';

import {Text, View} from '../components/Themed';
import Optional from '../components/Optional';

export default function MainScreen() {
    return (
        <View style={styles.container}>
            <Text style={styles.title}>Узнай что хочешь!</Text>
            <View style={styles.separator} lightColor="#473" darkColor="rgba(28,156,2,0.1)"/>
            <ScrollView style={styles.scroll}>
                <Optional btnTitle="Button 1"/>
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: 30,
        width: "100%",
        flex: 1,
        backgroundColor: "#8bc34a"
    },
    title: {
        fontSize: 20,
        marginVertical: 3,
        fontWeight: 'bold',
        alignSelf: "center",
        color: "#005b01",
    },
    separator: {
        height: 3,
        width: "80%",
        marginVertical: 2,
        //backgroundColor: "blue"
    },
    scroll: {
        alignContent: 'center',
        width: '100%',
        backgroundColor: "#8bc34a"
    },
});
