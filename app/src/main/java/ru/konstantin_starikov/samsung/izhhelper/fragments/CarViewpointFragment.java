package ru.konstantin_starikov.samsung.izhhelper.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import ru.konstantin_starikov.samsung.izhhelper.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarViewpointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarViewpointFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Stack<Image> photos;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarViewpointFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarViewpointFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarViewpointFragment newInstance(String param1, String param2) {
        CarViewpointFragment fragment = new CarViewpointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        photos = new Stack<Image>();
    }

    public void addPhotoToProcessing(Image photo)
    {
        photos.push(photo);
    }

    public void updateUserPosition()
    {
        Image processedPhoto;
        if(!photos.empty()) processedPhoto = photos.pop();
        //здесь будем менять положение положение пользователя относительно автомобиля
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_viewpoint, container, false);
    }
}