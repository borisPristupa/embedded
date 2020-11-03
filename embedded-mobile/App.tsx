import React from 'react';

import useCachedResources from './hooks/useCachedResources';
import MainScreen from "./screens/MainScreen";

export default function App() {
    const isLoadingComplete = useCachedResources();

    if (!isLoadingComplete) {
        return null;
    } else {
        return <MainScreen/>;
    }
}
