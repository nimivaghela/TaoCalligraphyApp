package com.hbb20;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    public static void openCountryCodeDialog(final CountryCodePicker codePicker, String selectCountryLabel, String searchLabel, String nodataFound) {
        final Context context = codePicker.getContext();
        final Dialog dialog = new Dialog(context);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<Country> masterCountries = Country.getCustomMasterCountryList(context, codePicker);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);

        //keyboard
        if (codePicker.isSearchAllowed() && codePicker.isDialogKeyboardAutoPopup()) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        //dialog views
        RecyclerView recyclerView_countryDialog = (RecyclerView) dialog.findViewById(R.id.recycler_countryDialog);
        final TextView textViewTitle = (TextView) dialog.findViewById(R.id.textView_title);
        RelativeLayout rlQueryHolder = (RelativeLayout) dialog.findViewById(R.id.rl_query_holder);
        ImageView imgClearQuery = (ImageView) dialog.findViewById(R.id.img_clear_query);
        final EditText editText_search = (EditText) dialog.findViewById(R.id.editText_search);
        TextView textView_noResult = (TextView) dialog.findViewById(R.id.textView_noresult);
        RelativeLayout rlHolder = (RelativeLayout) dialog.findViewById(R.id.rl_holder);
        ImageView imgDismiss = (ImageView) dialog.findViewById(R.id.img_dismiss);

        editText_search.clearFocus();
        editText_search.setHint("" + searchLabel);
        textViewTitle.setText("" + selectCountryLabel);

        // type faces
        //set type faces
        try {
            if (codePicker.getDialogTypeFace() != null) {
                if (codePicker.getDialogTypeFaceStyle() != CountryCodePicker.DEFAULT_UNSET) {
                    textView_noResult.setTypeface(codePicker.getDialogTypeFace(), codePicker.getDialogTypeFaceStyle());
                    editText_search.setTypeface(codePicker.getDialogTypeFace(), codePicker.getDialogTypeFaceStyle());
                    textViewTitle.setTypeface(codePicker.getDialogTypeFace(), codePicker.getDialogTypeFaceStyle());
                } else {
                    textView_noResult.setTypeface(codePicker.getDialogTypeFace());
                    editText_search.setTypeface(codePicker.getDialogTypeFace());
                    textViewTitle.setTypeface(codePicker.getDialogTypeFace());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dialog background color
        if (codePicker.getDialogBackgroundColor() != 0) {
            rlHolder.setBackgroundColor(codePicker.getDialogBackgroundColor());
        }

        //close button visibility
        if (codePicker.isShowCloseIcon()) {
            imgDismiss.setVisibility(View.VISIBLE);
            imgDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            imgDismiss.setVisibility(View.GONE);
        }
        //clear button color and title color
        if (codePicker.getDialogTextColor() != 0) {
            int textColor = codePicker.getDialogTextColor();
            imgClearQuery.setColorFilter(textColor);
            imgDismiss.setColorFilter(textColor);
            textViewTitle.setTextColor(textColor);
            textView_noResult.setTextColor(textColor);
            editText_search.setTextColor(textColor);
            editText_search.setHintTextColor(Color.argb(100, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
        }
        //editText tint
        if (codePicker.getDialogSearchEditTextTintColor() != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText_search.setBackgroundTintList(ColorStateList.valueOf(codePicker.getDialogSearchEditTextTintColor()));
            }
        }
        //add messages to views

        //add messages to views
        textView_noResult.setText("" + nodataFound);

        textViewTitle.setText(codePicker.getDialogTitle());
        editText_search.setHint(codePicker.getSearchHintText());
        textView_noResult.setText(codePicker.getNoResultFoundText());

        //this will make dialog compact
        if (!codePicker.isSearchAllowed()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView_countryDialog.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView_countryDialog.setLayoutParams(params);
        }

        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, rlQueryHolder, editText_search, textView_noResult, dialog, imgClearQuery);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);

        //fast scroller
        FastScroller fastScroller = (FastScroller) dialog.findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(recyclerView_countryDialog);
        if (codePicker.isShowFastScroller()) {
            if (codePicker.getFastScrollerBubbleColor() != 0) {
                fastScroller.setBubbleColor(codePicker.getFastScrollerBubbleColor());
            }

            if (codePicker.getFastScrollerHandleColor() != 0) {
                fastScroller.setHandleColor(codePicker.getFastScrollerHandleColor());
            }

            if (codePicker.getFastScrollerBubbleTextAppearance() != 0) {
                try {
                    fastScroller.setBubbleTextAppearance(codePicker.getFastScrollerBubbleTextAppearance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            fastScroller.setVisibility(View.GONE);
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideKeyboard(context);
                if (codePicker.getDialogEventsListener() != null) {
                    codePicker.getDialogEventsListener().onCcpDialogDismiss(dialogInterface);
                }
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                hideKeyboard(context);
                if (codePicker.getDialogEventsListener() != null) {
                    codePicker.getDialogEventsListener().onCcpDialogCancel(dialogInterface);
                }
            }
        });

        dialog.show();
        if (codePicker.getDialogEventsListener() != null) {
            codePicker.getDialogEventsListener().onCcpDialogOpen(dialog);
        }
    }

    private static void hideKeyboard(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
