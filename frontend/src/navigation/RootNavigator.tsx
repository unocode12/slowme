import React from "react";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { HomeScreen } from "../screens/HomeScreen";
import { SoloScreen } from "../screens/SoloScreen";
import { RoomsScreen } from "../screens/RoomsScreen";
import { CreateRoomScreen } from "../screens/CreateRoomScreen";
import { RoomScreen } from "../screens/RoomScreen";
import type { RootStackParamList } from "../types/nav";
import { colors } from "../theme/colors";

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootNavigator() {
  return (
    <Stack.Navigator
      initialRouteName="Home"
      screenOptions={{
        headerStyle: { backgroundColor: colors.bg },
        headerTintColor: colors.text,
        contentStyle: { backgroundColor: colors.bg },
      }}
    >
      <Stack.Screen
        name="Home"
        component={HomeScreen}
        options={{ headerShown: false }}
      />
      <Stack.Screen
        name="Solo"
        component={SoloScreen}
        options={{ title: "혼자 놀기" }}
      />
      <Stack.Screen
        name="Rooms"
        component={RoomsScreen}
        options={{ title: "같이 놀기" }}
      />
      <Stack.Screen
        name="CreateRoom"
        component={CreateRoomScreen}
        options={{ title: "방 만들기" }}
      />
      <Stack.Screen
        name="Room"
        component={RoomScreen}
        options={{ title: "방" }}
      />
    </Stack.Navigator>
  );
}
