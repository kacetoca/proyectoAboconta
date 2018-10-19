/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.primefaces.component.export;

import javax.faces.FacesException;

public class ExporterFactory {

    private ExporterFactory() {
    }

    public static Exporter getExporterForType(String type, ExporterOptions options) {
        Exporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch (exporterType) {
                case XLS:
                    exporter = new ExcelExporter();
                    break;

                case PDF:
                    exporter = new CustomPDFExporter();
                    break;

               /* case CSV:
                    exporter = new CSVExporter(options);
                    break;

                case XML:
                    exporter = new XMLExporter();
                    break;

                case XLSX:
                    exporter = new ExcelXExporter();
                    break;
                case XLSXSTREAM:
                    exporter = new ExcelXStreamExporter();
                    break;*/

            }
        }
        catch (IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }
}
