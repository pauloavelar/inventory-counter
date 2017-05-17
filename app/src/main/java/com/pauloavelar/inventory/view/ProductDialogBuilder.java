package com.pauloavelar.inventory.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.pauloavelar.inventory.R;
import com.pauloavelar.inventory.dao.ProductDAO;

class ProductDialogBuilder {

    static void showProductDialog(Context context) {
        showProductDialog(context, null);
    }

    static void showProductDialog(Context context, String product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
            .setTitle(product == null ? R.string.add_new_product : R.string.rename_product)
            .setView(R.layout.product_dialog)
            .setPositiveButton(
                (product == null ? R.string.add_product : R.string.rename), buildListener(product))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static DialogInterface.OnClickListener buildListener(final String productName) {
        return new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog   = (AlertDialog) dialogInterface;
                EditText    editText = (EditText) dialog.findViewById(R.id.edit_new_product);
                Context     context  = editText.getContext();

                if (editText.getText().length() > 0) {
                    if (productName == null) {
                        ProductDAO.insert(context, editText.getText().toString());
                    } else {
                        ProductDAO.rename(context, productName, editText.getText().toString());
                        Toast.makeText(context, R.string.renaming, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.empty_product, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}
