package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BooksContract;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    private long id;

    /**
     * TextView field to enter the book's name
     */
    private TextView mNameTextView;

    /**
     * TextView field to enter the book's price
     */
    private TextView mPriceTextView;

    /**
     * TextView field to enter the book's quantity
     */
    private TextView mQuantityTextView;

    /**
     * TextView field to enter the book's supplier name
     */
    private TextView mSupplierTextView;

    /**
     * TextView field to enter the book's supplier phone number
     */
    private TextView mPhoneTextView;

    //phone number variable to use for the phone intent
    private String phoneNumber;

    //quantityNumber variable for use with the increase and decrease buttons
    private int quantity;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Initialize a loader to read the pet data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        mNameTextView = (TextView) findViewById(R.id.details_book_name);
        mPriceTextView = (TextView) findViewById(R.id.details_book_price);
        mQuantityTextView = (TextView) findViewById(R.id.details_book_quantity);
        mSupplierTextView = (TextView) findViewById(R.id.details_supplier_name);
        mPhoneTextView = (TextView) findViewById(R.id.details_supplier_phone);

        //create button listeners for the increase and decrease buttons
        Button btnDecrease = findViewById(R.id.details_activity_button_decrease);
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    //create content values
                    quantity -= 1;
                    mQuantityTextView.setText(Integer.toString(quantity)+" copies");
                } else {
                    Toast.makeText(getApplicationContext(), R.string.button_decrease_limit, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnIncrease = (Button) findViewById(R.id.details_activity_button_increase);
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity += 1;
                mQuantityTextView.setText(Integer.toString(quantity)+" copies");
            }
        });

    }

    public void saveBook() {
        //find the quantity value as it stands
        String quantityString = mQuantityTextView.getText().toString().trim();

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BooksContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityString);

        //this is an EXISTING book, so update the book with content URI: mCurrentBookUri
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because mCurrentBookUri will already identify the correct row in the database that
        // we want to modify.
        int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_update_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_details.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database (finish action in saveBook method)
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "CAll" menu option
            case R.id.action_call:
                // Pop up confirmation dialog for deletion
                callPhoneApp();
                return true;
            case R.id.action_edit:
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);

//                // Form the content URI that represents the specific book that was clicked on,
//                // by appending the "id" (passed as input to this method) onto the
//                // {@link BookEntry#CONTENT_URI}.
//                // For example, the URI would be "content://com.example.android.inventoryApp/books/2"
//                // if the pet with ID 2 was clicked on.
//                Uri currentBookUri = ContentUris.withAppendedId(BooksContract.BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
                //respond to a click on the up button of the navigation bar
                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform an implicit intent to a phone app with the supplier's phone number.
     */
    private void callPhoneApp() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void sendToEditActivity(long id) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BooksContract.BookEntry._ID,
                BooksContract.BookEntry.COLUMN_BOOK_NAME,
                BooksContract.BookEntry.COLUMN_BOOK_PRICE,
                BooksContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BooksContract.BookEntry.COLUMN_BOOK_SUPPLIER,
                BooksContract.BookEntry.COLUMN_BOOK_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,               // Query the content URI for the current book
                projection,                   // Columns to include in the resulting Cursor
                null,                 // No selection clause
                null,             // No selection arguments
                null);               // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BooksContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BooksContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BooksContract.BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BooksContract.BookEntry.COLUMN_BOOK_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            phoneNumber = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameTextView.setText(name);
            mPriceTextView.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity) + " copies");
            mSupplierTextView.setText(supplier);
            mPhoneTextView.setText(phoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierTextView.setText("");
        mPhoneTextView.setText("");
    }
}