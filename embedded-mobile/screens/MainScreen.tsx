import * as React from 'react';
import {ScrollView, StyleSheet, ImageBackground} from 'react-native';

import {Text, View} from '../components/Themed';
import DataView from '../components/DataView';

export default function MainScreen() {
    return (
        <ImageBackground source={require('../assets/images/default.jpg')} style={styles.container}>
            <Text style={styles.title}>Узнай что хочешь!</Text>
            <View style={styles.separator} lightColor="#473" darkColor="rgba(28,156,2,0.1)"/>
            <ScrollView style={styles.scroll}>
                <DataView/>
            </ScrollView>
        </ImageBackground>
    );
}

const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        justifyContent: 'center',
        width: "100%",
        flex: 1,
    },
    title: {
        fontSize: 20,
        marginVertical: 3,
        fontWeight: 'bold',
        alignSelf: "center",
        color: "white",
    },
    separator: {
        height: 3,
        width: "80%",
        marginVertical: 2,
        backgroundColor: "white",
    },
    scroll: {
        alignContent: 'center',
        width: '100%',
    },
});
