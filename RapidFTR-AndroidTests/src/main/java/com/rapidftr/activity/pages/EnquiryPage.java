package com.rapidftr.activity.pages;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.RobotiumUtils;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;
import com.rapidftr.view.fields.TextField;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;

public class EnquiryPage {
    public Solo solo;
    public RobotiumUtils rutils;
    int formPosition;

    public EnquiryPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToCreatePage() {
        solo.clickOnText("Enquiry");
        solo.waitForText("Enquiry details");
    }

    public List<String> getAllFormFields() {
        List<String> texts = new ArrayList<String>();
        ArrayList<View> views = rutils.removeInvisibleViews(solo.getViews());
        for (View v : views) {
            if (v instanceof TextView) {
                String text = ((TextView)v).getText().toString();
                texts.add(text);
            }
        }
        return texts;
    }

    public List<String> getAllFormSections() {
        solo.clickOnText("Enquirer Details",0);
        solo.waitForText("Tracing Information");
        ListAdapter adapter = solo.getCurrentViews(ListView.class).get(0).getAdapter();
        int totalCount = adapter.getCount();
        List<String> formSections = new ArrayList<String>();
        for(int i=0;i<totalCount;i++){
            formSections.add(adapter.getItem(i).toString());
        }
        return formSections;
    }

    public void selectFormSection(String formSectionName) {
        solo.waitForText("Save");
        solo.clickOnView(solo.getCurrentViews(Spinner.class).get(0));
        solo.waitForText(formSectionName);
        ListAdapter adapter= solo.getCurrentViews(ListView.class).get(0).getAdapter();
        for(int i=0;i<adapter.getCount();i++){
            if(adapter.getItem(i).toString().equalsIgnoreCase(formSectionName)){
                formPosition=i;
                break;
            }
        }
        solo.clickOnText(adapter.getItem(formPosition).toString());
        solo.waitForText(formSectionName);
        solo.sleep(3);
    }

    public void verifyFieldsDisplayed(List<String> formFields) {
        List<String> visibleText = getVisibleText();
        for (Object fieldName : formFields) {
            assertEquals(format("Visibility of field %s", fieldName),
                    true, visibleText.contains(fieldName));
        }
    }

    public List<String> getVisibleText(){
        List<String> texts = new ArrayList<String>();
        ArrayList<View> views = rutils.removeInvisibleViews(solo.getViews());
        for (View v : views) {
            if (v instanceof TextView) {
                String text = ((TextView)v).getText().toString();
                texts.add(text);
            }
        }
        return texts;
    }

    public void enterEnquirerDetails(List<String> enquirerDetails) {
        TextField textField = (TextField) solo.getCurrentActivity().findViewById("enquirer_name".hashCode());
        EditText nameField = (EditText) textField.findViewById(R.id.value);
        solo.enterText(nameField, "");
        solo.enterText(nameField, enquirerDetails.get(0).toString());
    }

    public void enterFamilyDetails(List<String> familyDetails) {
        ArrayList<EditText> editTexts = solo.getCurrentViews(EditText.class);
        for (int i = 0; i < familyDetails.size() && i < editTexts.size(); i++) {
            solo.enterText(editTexts.get(i), familyDetails.get(i));
        }

    }



    public void save() {
        solo.clickOnButton("Save");
    }

    public void verifyEnquirerDetails(List<String> enquirerDetails) {
        solo.searchButton("Edit", true);
        selectFormSection("Enquirer Details");
        Assert.assertTrue(solo.searchEditText(enquirerDetails.get(0).toString()));
    }

    public void verifyNewEnquiryFormPresence() {
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                Activity currentActivity = solo.getCurrentActivity();
                View enquirerNameView = currentActivity.findViewById("enquirer_name".hashCode());
                EditText enquirerNameEditText = (null == enquirerNameView ? null : (EditText) enquirerNameView.findViewById(R.id.value) );
                String enquirerNameText = (null == enquirerNameEditText ? null : enquirerNameEditText.getText().toString());
                return (null != enquirerNameText) && ( "".equals(enquirerNameText));
            }
        }, 10000);
        solo.searchButton("Save");
    }

    public void assertPresenceOfValidationMessage() {
        TextField textField = (TextField) solo.getCurrentActivity().findViewById("enquirer_name".hashCode());
        EditText nameField = (EditText) textField.findViewById(R.id.value);
        assertEquals("Enquirer name is required", nameField.getError().toString());
    }

    public List<String> getAllEnquiryData() {
        List<String> allVisibleTexts = new ArrayList<String>();
        List<String> formSections = getAllFormSections();
        solo.clickOnText("Enquirer Details",0);
        for (String formSection : formSections){
            selectFormSection(formSection);
            List<String> visibleTexts = getVisibleText();
            for (String text: visibleTexts){
                allVisibleTexts.add(text);
            }
        }
        return allVisibleTexts;
    }
}
